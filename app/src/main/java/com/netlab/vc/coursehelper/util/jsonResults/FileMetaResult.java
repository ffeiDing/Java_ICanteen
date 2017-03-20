package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by Vc on 2016/12/22.
 */

public class FileMetaResult {
    FileMeta[] files;
    boolean success;

    public FileMeta[] getFiles() {
        return files;
    }

    public void setFiles(FileMeta[] files) {
        this.files = files;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
