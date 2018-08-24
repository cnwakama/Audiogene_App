package chibuzo.nwakama.audiogene_collection;

public class FileWrapper
{

  private final String image_name;

  private String encodedFile;

  public FileWrapper(String userName, String encodedFile)
  {
    this.image_name = userName;
    this.encodedFile = encodedFile;
  }

  public String getEncodedFile()
  {
    return encodedFile;
  }

  public void setEncodedFile(String encodedFile)
  {
    this.encodedFile = encodedFile;
  }

  public String getImage_name()
  {
    return image_name;
  }

}
