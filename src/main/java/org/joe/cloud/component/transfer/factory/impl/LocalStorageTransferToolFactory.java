package org.joe.cloud.component.transfer.factory.impl;

import org.joe.cloud.component.transfer.download.Downloader;
import org.joe.cloud.component.transfer.download.product.LocalStorageDownloader;
import org.joe.cloud.component.transfer.factory.TransferToolFactory;
import org.joe.cloud.component.transfer.upload.Uploader;
import org.joe.cloud.component.transfer.upload.product.LocalStorageUploader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Component
public class LocalStorageTransferToolFactory implements TransferToolFactory {
    @Resource
    private LocalStorageUploader localStorageUploader;
    @Resource
    private LocalStorageDownloader localStorageDownloader;

    @Override
    public Uploader getUploader() {
        return localStorageUploader;
    }

    @Override
    public Downloader getDownloader() {
        return localStorageDownloader;
    }
}
