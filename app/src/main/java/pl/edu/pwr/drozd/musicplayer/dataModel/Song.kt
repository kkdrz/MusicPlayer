package pl.edu.pwr.drozd.musicplayer.dataModel

import android.content.ContentUris
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class Song(val id: Long, val title: String, val author: String) : Parcelable {
    fun getURI(): Uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Song> = object : Parcelable.Creator<Song> {
            override fun createFromParcel(source: Parcel): Song = Song(source)
            override fun newArray(size: Int): Array<Song?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readLong(),
    source.readString(),
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(title)
        dest.writeString(author)
    }
}
