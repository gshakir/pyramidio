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
package gov.nist.isg.archiver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

/**
 *
 * @author Gulam D. Shakir
 */
public class S3Archiver implements FilesArchiver {

    private final String bucket;
    private final String prefix;

    public S3Archiver(URI outputURI) throws Exception {
        bucket = outputURI.getHost();
        prefix = outputURI.getPath().substring(1);
        AmazonS3Client client = new AmazonS3Client();
        ObjectListing listing = client.listObjects(bucket, prefix);
        if (!listing.getObjectSummaries().isEmpty()) {
            //throw new IllegalStateException("The bucket path exists: " + bucket + "/" + prefix);
        }
    }

    @Override
    public <T> T appendFile(String path, FileAppender<T> appender)
            throws IOException {
        final String s3key = prefix + "/" + path;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        T result = appender.append(out);
        AmazonS3Client client = new AmazonS3Client();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(out.size());
        PutObjectResult putResult = client.putObject(bucket, s3key, new ByteArrayInputStream(out.toByteArray()), metadata);
        System.out.println("Done Put for s3Key: " + s3key);
        System.out.println("Done Put Result: " + putResult.getContentMd5());
        return result;

        /*
        PipedInputStream in = new PipedInputStream(1024 * 100);
        PipedOutputStream out = new PipedOutputStream(in);
        T result = appender.append(out);
        AmazonS3Client client = new AmazonS3Client();
        PutObjectResult putResult = client.putObject(bucket, s3key, in, new ObjectMetadata());
        System.out.println("Put Result: " + putResult.getContentMd5());
        out.close();
        in.close();
        System.out.println("Done Put Result: " + putResult.getContentMd5());
        return result;
        */
    }

    @Override
    public <T> T appendBigFile(String path, FileAppender<T> appender)
            throws IOException {
        return appendFile(path, appender);
    }

    @Override
    public void appendFile(String path, File file) throws IOException {
        /*
        try (final FileInputStream fis = new FileInputStream(file)) {
            appendFile(path, new FileAppender<Void>() {
                @Override
                public Void append(OutputStream outputStream) throws IOException {
                    IOUtils.copy(fis, outputStream);
                    return null;
                }
            });
        }
        */
    }

    @Override
    public synchronized void close() throws IOException {
    }
}
