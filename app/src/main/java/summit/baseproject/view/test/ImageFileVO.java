package summit.baseproject.view.test;

public class ImageFileVO {
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImgString() {
        return imgString;
    }

    public void setImgString(String imgString) {
        this.imgString = imgString;
    }

    private String fileName;
    private String imgString;

    public ImageFileVO( String fileName, String imgString ){
        this.fileName = fileName;
        this.imgString = imgString;
    }
}
