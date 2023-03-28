package com.flysolo.dmmsugradelevelapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Content implements Parcelable{
   String id;
   String lessonID;
   String title;
   String description;
   String attachment;
   Long createdAt;
   public Content() {}

   public Content(String id, String lessonID, String title, String description, String attachment, Long createdAt) {
      this.id = id;
      this.lessonID = lessonID;
      this.title = title;
      this.description = description;
      this.attachment = attachment;
      this.createdAt = createdAt;
   }

   protected Content(Parcel in) {
      id = in.readString();
      lessonID = in.readString();
      title = in.readString();
      description = in.readString();
      attachment = in.readString();
      if (in.readByte() == 0) {
         createdAt = null;
      } else {
         createdAt = in.readLong();
      }
   }

   public static final Creator<Content> CREATOR = new Creator<Content>() {
      @Override
      public Content createFromParcel(Parcel in) {
         return new Content(in);
      }

      @Override
      public Content[] newArray(int size) {
         return new Content[size];
      }
   };

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getLessonID() {
      return lessonID;
   }

   public void setLessonID(String lessonID) {
      this.lessonID = lessonID;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getAttachment() {
      return attachment;
   }

   public void setAttachment(String attachment) {
      this.attachment = attachment;
   }

   public Long getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(Long createdAt) {
      this.createdAt = createdAt;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeString(id);
      parcel.writeString(lessonID);
      parcel.writeString(title);
      parcel.writeString(description);
      parcel.writeString(attachment);
      if (createdAt == null) {
         parcel.writeByte((byte) 0);
      } else {
         parcel.writeByte((byte) 1);
         parcel.writeLong(createdAt);
      }
   }
}
