package org.joe.cloud.component.transfer.factory;

import org.joe.cloud.component.transfer.download.Downloader;
import org.joe.cloud.component.transfer.upload.Uploader;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */

public interface TransferToolFactory {
    Uploader getUploader();

    Downloader getDownloader();
}
