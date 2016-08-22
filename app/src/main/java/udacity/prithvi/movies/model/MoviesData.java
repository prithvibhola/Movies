package udacity.prithvi.movies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MoviesData implements Parcelable {

    private int id;
    private String title, tagLine, status, releaseDate, genre, image, coverImage, overview, language;
    private int duration;
    private float popularity, rating;

    public static final Creator<MoviesData> CREATOR = new Creator<MoviesData>() {
        @Override
        public MoviesData createFromParcel(Parcel in) {
            return new MoviesData(in);
        }

        @Override
        public MoviesData[] newArray(int size) {
            return new MoviesData[size];
        }
    };

    public MoviesData(){

    }

    public MoviesData(int id, String title, String image, float rating, int duration){
        this.id = id;
        this.title = title;
        this.image = image;
        this.rating = rating;
        this.duration = duration;
    }

    public MoviesData(int id, String image, String title, String genre, float popularity, float rating){
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.image = image;
        this.popularity = popularity;
        this.rating = rating;
    }

    public MoviesData(Parcel in) {

        String[] data = new String[13];
        in.readStringArray(data);
        this.id = Integer.valueOf(data[0]);
        this.title = data[1];
        this.tagLine = data[2];
        this.status = data[3];
        this.releaseDate = data[4];
        this.genre = data[5];
        this.image = data[6];
        this.coverImage = data[7];
        this.duration = Integer.parseInt(data[8]);
        this.popularity = Float.parseFloat(data[9]);
        this.overview = data[10];
        this.rating = Float.parseFloat(data[11]);
        this.language = data[12];
    }

    public MoviesData(int id, String title, String tagLine, String status, String releaseDate, String genre, String image, String coverImage, int duration, float popularity, String overview, float rating, String language){
        this.id = id;
        this.title = title;
        this.tagLine = tagLine;
        this.status = status;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.image = image;
        this.coverImage = coverImage;
        this.duration = duration;
        this.popularity = popularity;
        this.overview = overview;
        this.rating = rating;
        this.language = language;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return  id;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return title;
    }

    public void setTagLine(String tagLine){
        this.tagLine = tagLine;
    }
    public String getTagLine(){
        return tagLine;
    }

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return status;
    }

    public void setReleasedate(String releaseDate){
        this.releaseDate = releaseDate;
    }
    public String getReleasedate(){
        return releaseDate;
    }

    public void setGenre(String genre){
        this.genre = genre;
    }
    public String getGenre(){
        return genre;
    }

    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return image;
    }

    public void setCoverImage(String coverImage){
        this.coverImage = coverImage;
    }
    public String getCoverImage(){
        return coverImage;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }
    public int getDuration(){
        return duration;
    }

    public void setPopularity(float popularity){
        this.popularity = popularity;
    }
    public float getPopularity(){
        return popularity;
    }

    public void setOverview(String overview){
        this.overview = overview;
    }
    public String getOverview(){
        return overview;
    }

    public void setRating(float rating){
        this.rating = rating;
    }
    public float getRating(){
        return rating;
    }

    public void setLanguage(String language){
        this.language = language;
    }
    public String getLanguage(){
        return language;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(tagLine);
        dest.writeString(status);
        dest.writeString(releaseDate);
        dest.writeString(genre);
        dest.writeString(image);
        dest.writeString(coverImage);
        dest.writeInt(duration);
        dest.writeFloat(popularity);
        dest.writeString(overview);
        dest.writeFloat(rating);
        dest.writeString(language);
    }
}
