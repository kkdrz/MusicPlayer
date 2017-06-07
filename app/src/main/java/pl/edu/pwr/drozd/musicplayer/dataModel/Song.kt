package pl.edu.pwr.drozd.musicplayer.dataModel

import android.content.ContentUris
import android.net.Uri

data class Song(val id: Long, val title: String, val author: String) {

    fun getURI(): Uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
}
