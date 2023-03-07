package com.flysolo.dmmsugradelevelapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Content implements Parcelable {
   String content;
   String attachment;

   public Content() {}
   public Content(String content, String attachment) {
      this.content = content;
      this.attachment = attachment;
   }

   protected Content(Parcel in) {
      content = in.readString();
      attachment = in.readString();
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

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getAttachment() {
      return attachment;
   }

   public void setAttachment(String attachment) {
      this.attachment = attachment;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeString(content);
      parcel.writeString(attachment);
   }
}
