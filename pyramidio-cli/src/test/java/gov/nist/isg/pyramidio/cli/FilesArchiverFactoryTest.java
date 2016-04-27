/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package gov.nist.isg.pyramidio.cli;

import org.junit.Test;
import static org.junit.Assert.*;

import gov.nist.isg.archiver.DirectoryArchiver;
import gov.nist.isg.archiver.FilesArchiver;
import gov.nist.isg.archiver.TarArchiver;
import gov.nist.isg.archiver.HdfsArchiver;
import gov.nist.isg.archiver.TarOnHdfsArchiver;

public class FilesArchiverFactoryTest {

    @Test
    public void testNoScheme() throws Exception {
        FilesArchiver archiver = FilesArchiverFactory.makeFilesArchiver("testfolder");
        assertEquals(archiver.getClass().toString() , DirectoryArchiver.class.toString());
        assertEquals(archiver.getClass(), DirectoryArchiver.class);
    }

    @Test
    public void testFileScheme() throws Exception {
        FilesArchiver archiver = FilesArchiverFactory.makeFilesArchiver("file:///tmp/testfolder");
        assertEquals(archiver.getClass().toString() , DirectoryArchiver.class.toString());
        assertEquals(archiver.getClass(), DirectoryArchiver.class);
    }

    @Test
    public void testFileTarScheme() throws Exception {
        FilesArchiver archiver = FilesArchiverFactory.makeFilesArchiver("file:///tmp/testfolder.tar");
        assertEquals(archiver.getClass(), TarArchiver.class);
    }

    @Test
    public void testHdfsScheme() throws Exception {
        FilesArchiver archiver = FilesArchiverFactory.makeFilesArchiver("hdfs://localhost:9000/testfolder");
        assertEquals(archiver.getClass(), HdfsArchiver.class);
    }

    @Test
    public void testHdfsTarScheme() throws Exception {
        FilesArchiver archiver = FilesArchiverFactory.makeFilesArchiver("hdfs://localhost:9000/testfolder.tar");
        assertEquals(archiver.getClass(), TarOnHdfsArchiver.class);
    }
}
