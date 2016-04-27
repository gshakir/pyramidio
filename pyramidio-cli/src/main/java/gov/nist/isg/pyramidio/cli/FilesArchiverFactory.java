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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.commons.io.FilenameUtils;

import gov.nist.isg.archiver.DirectoryArchiver;
import gov.nist.isg.archiver.FilesArchiver;
import gov.nist.isg.archiver.TarArchiver;
import gov.nist.isg.archiver.HdfsArchiver;
import gov.nist.isg.archiver.TarOnHdfsArchiver;
import gov.nist.isg.archiver.SequenceFileArchiver;
import gov.nist.isg.archiver.S3Archiver;

public class FilesArchiverFactory {


    private static final String S3_SCHEME = "s3";
    private static final String FILE_SCHEME = "file";
    private static final String HDFS_SCHEME = "hdfs";

    private static final String EMPTY_STRING = "";

    private static final String TAR_EXTENSION = "tar";
    private static final String SEQ_EXTENSION = "seq";

    public static FilesArchiver makeFilesArchiver(String outputFolder) throws Exception {
        FilesArchiver archiver = null;

        try {
            URI outputURI = new URI(outputFolder);
            System.out.println("Got scheme " + outputURI.getScheme() + " for folder " + outputFolder);

            if (outputURI.getScheme() == null ||
                outputURI.getScheme().equalsIgnoreCase(FILE_SCHEME) ||
                outputURI.getScheme().equalsIgnoreCase(EMPTY_STRING)) {
                archiver = makeDirectoryArchiver(outputFolder, outputURI);
            }
            else if (outputURI.getScheme().equalsIgnoreCase(HDFS_SCHEME)) {
                archiver = makeHdfsArchiver(outputFolder);
            }
            else if (outputURI.getScheme().equalsIgnoreCase(S3_SCHEME)) {
                archiver = makeS3Archiver(outputURI, outputFolder);
            }
            else {
                throw new IllegalStateException("The URI is not one of s3/file/hdfs scheme " + outputFolder);
            }
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to parse the URI for: " + outputFolder);
        }
        return archiver;
    }

    private static FilesArchiver makeDirectoryArchiver(String outputFolder, URI outputURI) throws Exception {
        String extension = FilenameUtils.getExtension(outputFolder);
        FilesArchiver archiver = null;
        if (extension.contains(TAR_EXTENSION)) {
            archiver =  new TarArchiver(new File(outputURI.getPath()));
        }
        else {
            if (outputURI.getScheme() == null ||
                    outputURI.getScheme().equalsIgnoreCase(EMPTY_STRING)) { 
                archiver = new DirectoryArchiver(new File(outputFolder));
            }
            else {
                archiver = new DirectoryArchiver(new File(outputURI));
            }
        }
        return archiver;
    }

    private static FilesArchiver makeHdfsArchiver(String outputFolder) throws Exception {
        String extension = FilenameUtils.getExtension(outputFolder);
        Configuration conf = new Configuration();
        FilesArchiver archiver = null;
        Path path = new Path(outputFolder);
        if (extension.contains(TAR_EXTENSION)) {
            archiver = new TarOnHdfsArchiver(path, conf);
        }
        else if (extension.contains(SEQ_EXTENSION)) {
            archiver = new SequenceFileArchiver(path, conf);
        }
        else {
            archiver = new HdfsArchiver(path, conf);
        }
        return archiver;
    }

    private static FilesArchiver makeS3Archiver(URI outputURI, String outputFolder) throws Exception {
            return new S3Archiver(outputURI);
        
    }
}

