package com.hdvideo.allformats.player.Extras;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.Models.VideoInfo;

import java.util.List;
import java.util.Map;

public class AppAsyncTask {
    static ProgressDialog progressDialog;
    public static class AllVideos extends AsyncTask {

        Activity activity;

        AppInterfaces.AllVideosListener allVideosListener ;

        List<VideoInfo> videoInfoList;

        public AllVideos(Activity activity, AppInterfaces.AllVideosListener allVideosListener) {
            this.activity = activity;
            this.allVideosListener = allVideosListener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
           videoInfoList = Utils.getVideoList(activity);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
               showProgressDialog(activity,"Loading");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            allVideosListener.getAllVideos(videoInfoList);
              hideProgressDialog();
        }

    }
    public static class VideoFolders extends AsyncTask {

        Activity activity;

        AppInterfaces.VideosFolderListener videosFolderListener ;

        Map<String, Integer> videoInfoList;

        public VideoFolders(Activity activity, AppInterfaces.VideosFolderListener videosFolderListener) {
            this.activity = activity;
            this.videosFolderListener = videosFolderListener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
           videoInfoList = Utils.getVideoFoldersWithCount(activity);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
               showProgressDialog(activity,"Loading");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            videosFolderListener.getVideosFolder(videoInfoList);
              hideProgressDialog();
        }

    }
    public static class Albums extends AsyncTask {

        Activity activity;

        AppInterfaces.AlbumsListener videosFolderListener ;

        Map<String, Integer> videoInfoList;

        public Albums(Activity activity, AppInterfaces.AlbumsListener videosFolderListener) {
            this.activity = activity;
            this.videosFolderListener = videosFolderListener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
           videoInfoList = Utils.getAudioAlbumsWithCount(activity);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
               showProgressDialog(activity,"Loading");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            videosFolderListener.getAlbums(videoInfoList);
              hideProgressDialog();
        }

    }
    public static class Artists extends AsyncTask {

        Activity activity;

        AppInterfaces.ArtistsListener videosFolderListener ;

        Map<String, Integer> videoInfoList;

        public Artists(Activity activity, AppInterfaces.ArtistsListener videosFolderListener) {
            this.activity = activity;
            this.videosFolderListener = videosFolderListener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
           videoInfoList = Utils.getArtistsWithSongCount(activity);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
               showProgressDialog(activity,"Loading");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            videosFolderListener.getArtists(videoInfoList);
              hideProgressDialog();
        }

    }
    public static class AllSongs extends AsyncTask {

        Activity activity;

        AppInterfaces.AllAudiosListener allAudiosListener ;

        List<AudioInfo> audioInfoList;

        public AllSongs(Activity activity, AppInterfaces.AllAudiosListener allAudiosListener) {
            this.activity = activity;
            this.allAudiosListener = allAudiosListener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            audioInfoList = Utils.getAllAudioFiles(activity);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
               showProgressDialog(activity,"Loading");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            allAudiosListener.getAllAudios(audioInfoList);
              hideProgressDialog();
        }

    }

    public static void showProgressDialog(Context context, String msg) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(null);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        try {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
