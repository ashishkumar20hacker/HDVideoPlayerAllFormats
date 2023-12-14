package com.hdvideo.allformats.player.Extras;


import static android.os.Build.VERSION.SDK_INT;

import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainAudioInfoList;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainNewFileName;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainOldFilePath;
import static com.hdvideo.allformats.player.Activity.DashboardActivity.mainVideoInfoList;
import static com.hdvideo.allformats.player.Extras.Constants.downloadWhatsAppDir;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.play.core.review.ReviewManager;
import com.hdvideo.allformats.player.Activity.ResultActivity;
import com.hdvideo.allformats.player.Models.AudioInfo;
import com.hdvideo.allformats.player.Models.VideoInfo;
import com.hdvideo.allformats.player.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static ReviewManager reviewManager;
    private static final String TAG = "Utils";
    private static int editTxtId = 0;

    public static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public static final int RECORD_AUDIO_PERMISSION_REQ_CODE = 5678;
    public static final int STORAGE_PERMISSION_REQ_CODE = 100; // Define your request code
    public static final int NOTIFICATION_PERMISSION_REQ_CODE = 101; // Define your request code

    public static Activity myActivity;

    public static void ShowToast(Context context, String str) {
        try {
            Toast.makeText(context, "" + str, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + str, Toast.LENGTH_LONG).show();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void ShowToastError(Context context, String str) {
        try {
            Toast.makeText(context, "" + str, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + str, Toast.LENGTH_LONG).show();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static boolean isMyPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean copyFileInSavedDir(Context context, String sourceFile, boolean isWApp) {

        String finalPath = getDir(isWApp).getAbsolutePath();

        String pathWithName = finalPath + File.separator + new File(sourceFile).getName();
        Uri destUri = Uri.fromFile(new File(pathWithName));

        System.out.println("pathWithName" + pathWithName);
        InputStream is = null;
        OutputStream os = null;
        try {
            Uri uri = Uri.parse(sourceFile);
            is = context.getContentResolver().openInputStream(uri);
            os = context.getContentResolver().openOutputStream(destUri, "w");

            byte[] buffer = new byte[1024];

            int length;
            while ((length = is.read(buffer)) > 0)
                os.write(buffer, 0, length);

            is.close();
            os.flush();
            os.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(destUri);
            context.sendBroadcast(intent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    static File getDir(boolean isWApp) {

        File rootFile = downloadWhatsAppDir;
        if (!isWApp) {
//            rootFile = downloadWABusiDir;
        }
        rootFile.mkdirs();

        return rootFile;

    }

    public static List<VideoInfo> getVideosFromFolder(Context context, String folderPath) {
        List<VideoInfo> videoInfoList = new ArrayList<>();

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA
        };
        String selection = MediaStore.Video.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%" + folderPath + "%"};
        String sortOrder = null;

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder)) {

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    long videoId = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    long videoSizeInBytes = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

                    // Convert bytes to megabytes
                    double videoSizeInMB = (videoSizeInBytes / (1024.0 * 1024.0));

                    VideoInfo videoInfo = new VideoInfo(videoId, videoName, videoSizeInMB, videoPath);
                    videoInfoList.add(videoInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videoInfoList;
    }


    public static ColorStateList setColorFromAttribute(Context context, int attrColor, int color) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrColor, typedValue, true);
        int colorRes = typedValue.resourceId != 0 ? typedValue.resourceId : typedValue.data;
        ColorStateList colorStateList = ContextCompat.getColorStateList(context, colorRes);
        if (colorStateList != null) return colorStateList;
        return ColorStateList.valueOf(ContextCompat.getColor(context, color));
    }

    public static int setAttributeColor(@NonNull Context context, int attrColor) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{attrColor});
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }

    public static void showRateApp(Activity contexts) {
        /*reviewManager = ReviewManagerFactory.create(contexts);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Getting the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(contexts, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown.
                });

                flow.addOnFailureListener(e -> {
                    rateApp(contexts);
                });
            }
        });*/
//        ReviewManager manager = BuildConfig.DEBUG ? new FakeReviewManager(contexts) : ReviewManagerFactory.create(contexts);
//        Task<ReviewInfo> request = manager.requestReviewFlow();
//        request.addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                ReviewInfo reviewInfo = task.getResult();
//                Task<Void> flow = manager.launchReviewFlow(contexts, reviewInfo);
//                flow.addOnCompleteListener(task1 -> {
//
//                });
//            }
//        });
    }

    public static boolean isRecordAudioPermissionGranted(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void requestRecordAudioPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_REQ_CODE);
        }
    }

    public static boolean isNotificationPermissionGranted(Activity activity) {
        return NotificationManagerCompat.from(activity).areNotificationsEnabled();
    }

    // Method to check if storage permission is granted
    public static boolean isStoragePermissionGranted(Activity activity) {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    // Method to request storage permission
    public static void requestStoragePermission(Activity activity) {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO}, STORAGE_PERMISSION_REQ_CODE);
        } else {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQ_CODE);
        }
    }

    public static void requestNotificationPermission(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS);
        activity.startActivityForResult(intent, NOTIFICATION_PERMISSION_REQ_CODE);
    }

    public static void requestOverlayPermission(Activity activity) {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
    }

    public static boolean canDrawOverlays(Context context) {
        if (SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else if (SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context)) {
            return true;
        }
        return false;
    }

    public static void gotoUrl(Activity activity) {
        Uri uri = Uri.parse(Constants.PRIVACY_POLICY);
        activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public static void rateApp(Activity contexts) {
        try {
            Intent rateIntent = rateIntentForUrl(contexts, "market://details");
            contexts.startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl(contexts, "https://play.google.com/store/apps/details");
            contexts.startActivity(rateIntent);
        }
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("except>>" + e);
            return false;
        }
    }

    private static Intent rateIntentForUrl(Activity contexts, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, contexts.getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public static void shareApp(Activity activity) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            String shareMessage = "Ready to join the offline video revolution? \n"
                    + "Don't miss out on the endless fun waiting for you with this All Video Downloader App!";
            shareMessage = shareMessage + "\n\n" + "https://play.google.com/store/apps/details?id=" + /*BuildConfig.APPLICATION_ID +*/ "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            activity.startActivity(Intent.createChooser(shareIntent, "Share Via"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    public static void copy(Context context, String str) {
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("label", str));
        Toast.makeText(context, "Text copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    public static String paste(Context context) {
        ClipboardManager clipBoardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData primaryClipData = clipBoardManager.getPrimaryClip();

        if (primaryClipData != null && primaryClipData.getItemCount() > 0) {
            String clip = primaryClipData.getItemAt(0).getText().toString();
            return clip;
        }
        return "";
    }

   /* public static List<DataModel> readJsonFromRaw(Context context, String type) {
        List<DataModel> jsonStringList = new ArrayList<>();

        try {
            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.captions);
            Scanner scanner = new Scanner(inputStream);

            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                List<String> content = new ArrayList<>();
                JSONObject object = (JSONObject) jsonArray.get(i);
                DataModel model = new DataModel(object.getString("title"), object.getString("type"));
                JSONArray array = object.getJSONArray("content");
                for (int j = 0; j < array.length(); j++) {
                    content.add(array.getString(j));
                }
                model.setContent(content);
                if (type.equals(model.getType())) jsonStringList.add(model);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        jsonStringList.sort((t1, t2) -> t1.getTitle().toLowerCase().compareToIgnoreCase(t2.getTitle().toLowerCase()));

        return jsonStringList;
    }*/

    public static void makeStatusBarTransparent(Activity context) {
        if (SDK_INT >= 19 && SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        //make fully Android Transparent Status bar
        if (SDK_INT >= 21) {
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void makeStatusBarTransparent2(Activity context) {
        if (SDK_INT >= 19 && SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (SDK_INT >= 21) {
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        /*WindowInsetsControllerCompat.setAppearanceLightStatusBars(true)*/
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static List<String> getFilesInDirectory(String directoryPath) {
        List<String> fileList = new ArrayList<>();

        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(file.getAbsolutePath());
                    }
                }
            }
        }

        return fileList;
    }

    public static void shareVideoFile(Context context, File destinationFile) {
        try {
            // ...
            Log.d("check", Uri.fromFile(destinationFile).toString());
            Uri uri2 = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    destinationFile
            );

//             Grant URI permission to other apps
            context.grantUriPermission(
                    context.getPackageName(),
                    uri2,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("video/*");
            share.putExtra(Intent.EXTRA_STREAM, uri2);

            // Revoke URI permissions after sharing
            context.revokeUriPermission(
                    uri2,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            context.startActivity(Intent.createChooser(share, "Share Video File"));
        } catch (Throwable t) {
            System.out.println("Throw>>" + t);
        }

    }

    private static void shareAudioFile(Context context, File destinationFile) {
        try {
            // ...
            Log.d("check", Uri.fromFile(destinationFile).toString());
            Uri uri2 = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    destinationFile
            );

//             Grant URI permission to other apps
            context.grantUriPermission(
                    context.getPackageName(),
                    uri2,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            share.putExtra(Intent.EXTRA_STREAM, uri2);

            // Revoke URI permissions after sharing
            context.revokeUriPermission(
                    uri2,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            context.startActivity(Intent.createChooser(share, "Share Audio File"));
        } catch (Throwable t) {
            System.out.println("Throw>>" + t);
        }
    }

    private static void copyFile(File sourceFile, File destinationFile) {
        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the file copy error
        }
    }

/*
    public static void shareAudioFile(Context context, String audioFilePath) {
        File audioFile = new File(audioFilePath);

        if (audioFile.exists()) {
            Uri contentUri = FileProvider.getUriForFile(
                    context,
                    "com.aivoice.tunechanger.voicechange.fileprovider",
                    audioFile
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("audio/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Share Audio File"));
        } else {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
        }
    }
*/

    /* public static boolean renameAudioFile(String oldFilePath, String newFileName) {
         File oldFile = new File(oldFilePath);

         if (oldFile.exists() && oldFile.isFile()) {
             // Extract the parent directory
             File parentDir = oldFile.getParentFile();

             // Create the new File with the desired name in the same directory
             File newFile = new File(parentDir, newFileName);

             // Perform the rename
             if (oldFile.renameTo(newFile)) {
                 return true;
             }
         }

         return false;
     }*/
    public static boolean renameAudioFile(String oldFilePath, String newFileName) {
        File oldFile = new File(oldFilePath);

        if (oldFile.exists() && oldFile.isFile()) {
            String extension = getFileExtension(oldFile.getName());
            if (extension.isEmpty()) {
                return false; // Unable to determine the file extension
            }

            File parentDir = oldFile.getParentFile();
            File newFile = new File(parentDir, newFileName + "." + extension);

            if (newFile.exists()) {
                // Log an error if the new file already exists
                Log.e("RenameAudioFile", "File with new name already exists");
                return false;
            }

            if (oldFile.renameTo(newFile)) {
                return true;
            } else {
                // Log an error if the rename operation fails
                Log.e("RenameAudioFile", "Rename operation failed");
            }
        } else {
            // Log an error if the old file doesn't exist or is not a file
            Log.e("RenameAudioFile", "Old file doesn't exist or is not a file");
        }

        return false;
    }

//    public static boolean renameAudioFile(String oldFilePath, String newFileName) {
//        File oldFile = new File(oldFilePath);
//
//        if (oldFile.exists() && oldFile.isFile()) {
//            String extension = getFileExtension(oldFile.getName()); // Get the file extension
//            if (extension.isEmpty()) {
//                return false; // Unable to determine the file extension
//            }
//
//            // Extract the parent directory
//            File parentDir = oldFile.getParentFile();
//
//            String newFilename = newFileName + "." + extension;
//            // Create the new File with the desired name and the same extension in the same directory
//            File newFile = new File(parentDir, newFilename);
//
//            // Perform the rename
//            if (oldFile.renameTo(newFile)) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    // Helper function to extract file extension
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    public static String removeTextBeforeDot(String input) {
        int dotIndex = input.indexOf('.');

        // Check if a dot is found and remove text before it
        if (dotIndex != -1) {
            return input.substring(dotIndex + 1);
        } else {
            // If no dot is found, return the original string
            return input;
        }
    }

    public static String readJsonFile(Context context, String filePath) {
        try {
            InputStream inputStream = context.getAssets().open(filePath);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

/*
    public static List<VoiceEffect> parseJson(String jsonString) {
        List<VoiceEffect> voiceEffects = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int id = jsonObject.getInt("id");
                String icon = jsonObject.getString("icon");
                String name = jsonObject.getString("name");
                int pitch = jsonObject.getInt("pitch");
                int rate = jsonObject.getInt("rate");
                int amplify = jsonObject.optInt("amplify", 0);
                boolean reverse = jsonObject.optBoolean("reverse", false);
                float[] echo = jsonArrayToFloatArray(jsonObject.optJSONArray("echo"));
                float[] reverb = jsonArrayToFloatArray(jsonObject.optJSONArray("reverb"));
                float[] filter = jsonArrayToFloatArray(jsonObject.optJSONArray("filter"));
                float[] eq1 = jsonArrayToFloatArray(jsonObject.optJSONArray("eq1"));
                float[] eq2 = jsonArrayToFloatArray(jsonObject.optJSONArray("eq2"));
                float[] eq3 = jsonArrayToFloatArray(jsonObject.optJSONArray("eq3"));
                float[] distort = jsonArrayToFloatArray(jsonObject.optJSONArray("distort"));
                float[] autowah = jsonArrayToFloatArray(jsonObject.optJSONArray("autowah"));
                float[] chorus = jsonArrayToFloatArray(jsonObject.optJSONArray("chorus"));
                float[] phaser = jsonArrayToFloatArray(jsonObject.optJSONArray("phaser"));

                VoiceEffect voiceEffect = new VoiceEffect();
                voiceEffect.setId(id);
                voiceEffect.setIcon(icon);
                voiceEffect.setName(name);
                voiceEffect.setPitch(pitch);
                voiceEffect.setRate(rate);
                voiceEffect.setAmplify(amplify);
                voiceEffect.setReverse(reverse);
                voiceEffect.setEcho(echo);
                voiceEffect.setReverb(reverb);
                voiceEffect.setFilter(filter);
                voiceEffect.setEq1(eq1);
                voiceEffect.setEq2(eq2);
                voiceEffect.setEq3(eq3);
                voiceEffect.setDistort(distort);
                voiceEffect.setAutowah(autowah);
                voiceEffect.setChorus(chorus);
                voiceEffect.setPhaser(phaser);

                voiceEffects.add(voiceEffect);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return voiceEffects;
    }
*/

    public static String milliSecFormat(long mill) {
        String str1;
        String str22;
        String str33;
        int ii = (int) (mill / 3600000);
        long j2 = mill % 3600000;
        int i2 = ((int) j2) / 60000;
        int roundData = Math.round((float) ((j2 % 60000) / 1000));
        if (ii > 0) {
            str1 = ii + ":";
        } else {
            str1 = "";
        }
        if (i2 < 10) {
            str22 = "0" + i2;
        } else {
            str22 = "" + i2;
        }
        if (roundData < 10) {
            str33 = "0" + roundData;
        } else {
            str33 = "" + roundData;
        }
        return str1 + str22 + ":" + str33;
    }

    public static boolean isMultipleOf3(int number) {
        return number % 3 == 0;
    }

    private static float[] jsonArrayToFloatArray(JSONArray jsonArray) throws JSONException {
        if (jsonArray != null) {
            int length = jsonArray.length();
            float[] floatArray = new float[length];
            for (int i = 0; i < length; i++) {
                floatArray[i] = (float) jsonArray.getDouble(i);
            }
            return floatArray;
        }
        return null;
    }


    public static boolean deleteFile(String filePath) {
        File fileToDelete = new File(filePath);

        if (fileToDelete.exists() && fileToDelete.isFile()) {
            return fileToDelete.delete();
        }

        return false;
    }

    public static void nextActivity(Activity activity, Class<?> className) {
        //       AdUtils.showInterstitialAd(adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
        //           @Override
//            public void adLoadState(boolean isLoaded) {
        activity.startActivity(new Intent(activity, className));
        activity.overridePendingTransition(0, 0);
//            }
//        });
    }

//    public static void nextActivity(Activity activity, Class<?> className, String key, DataModel value) {
//        //       AdUtils.showInterstitialAd(adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
//        //           @Override
////            public void adLoadState(boolean isLoaded) {
//        activity.startActivity(new Intent(activity, className).putExtra(key, value));
//        activity.overridePendingTransition(0, 0);
////            }
////        });
//    }


    public static void nextActivity(Activity activity, Class<?> className, String key, String value) {
        //       AdUtils.showInterstitialAd(adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
        //           @Override
//            public void adLoadState(boolean isLoaded) {
        activity.startActivity(new Intent(activity, className).putExtra(key, value));
        activity.overridePendingTransition(0, 0);
//            }
//        });
    }

    public static void nextActivity(Activity activity, Class<?> className, String key, boolean value) {
        //       AdUtils.showInterstitialAd(adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
        //           @Override
//            public void adLoadState(boolean isLoaded) {
        activity.startActivity(new Intent(activity, className).putExtra(key, value));
        activity.overridePendingTransition(0, 0);
//            }
//        });
    }

    public static void nextActivity(Activity activity, Class<?> className, String key, int value) {
        //       AdUtils.showInterstitialAd(adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
        //           @Override
//            public void adLoadState(boolean isLoaded) {
        activity.startActivity(new Intent(activity, className).putExtra(key, value));
        activity.overridePendingTransition(0, 0);
//            }
//        });
    }

    public static void nextFinishActivity(Activity activity, Class<?> className) {
        //       AdUtils.showInterstitialAd(adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
        //           @Override
//            public void adLoadState(boolean isLoaded) {
        activity.startActivity(new Intent(activity, className));
        activity.finish();
//            }
//        });
    }

    public static void nextFinishActivity(Activity activity, Class<?> className, String key, String value) {
        //       AdUtils.showInterstitialAd(adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
        //           @Override
//            public void adLoadState(boolean isLoaded) {
        activity.startActivity(new Intent(activity, className).putExtra(key, value));
        activity.finish();
//            }
//        });
    }

    public static void nextFinishActivity(Activity activity, Class<?> className, String key, boolean value) {
        //       AdUtils.showInterstitialAd(adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
        //           @Override
//            public void adLoadState(boolean isLoaded) {
        activity.startActivity(new Intent(activity, className).putExtra(key, value));
        activity.finish();
//            }
//        });
    }

    public static void nextFinishActivity(Activity activity, Class<?> className, String key, int value) {
        //       AdUtils.showInterstitialAd(adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
        //           @Override
//            public void adLoadState(boolean isLoaded) {
        activity.startActivity(new Intent(activity, className).putExtra(key, value));
        activity.finish();
//            }
//        });
    }

    public static void applyGradientOnTv(TextView tv, String color1, String color2) {
        Shader textShader = new LinearGradient(0, 0, tv.getPaint().measureText(tv.getText().toString()), tv.getTextSize(),
                new int[]{Color.parseColor(color1), Color.parseColor(color2)},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        tv.getPaint().setShader(textShader);
    }


    public interface onPushEffect {
        void onClick();
    }

    public static void pushEffectCardView(View btn, onPushEffect onPushEffect, boolean before) {
        btn.animate()
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (before) {
                            onPushEffect.onClick();
                        }
                        btn.animate()
                                .scaleY(1f)
                                .scaleX(1f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        if (!before) {
                                            onPushEffect.onClick();
                                        }
                                    }
                                })
                                .setDuration(50)
                                .setInterpolator(new AccelerateDecelerateInterpolator());
                    }
                })
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(50)
                .setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public static Map<String, Integer> getVideoFoldersWithCount(Context context) {
        Map<String, Integer> videoFoldersWithCount = new HashMap<>();

        String[] projection = {MediaStore.Video.Media.DATA};
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(videoUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    String folderPath = videoPath.substring(0, videoPath.lastIndexOf("/")); // Extract folder path

                    // Increment count for each folder
                    int count = videoFoldersWithCount.getOrDefault(folderPath, 0);
                    videoFoldersWithCount.put(folderPath, count + 1);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return videoFoldersWithCount;
    }

    public static List<AudioInfo> getAllAudioFiles(Context context) {
        List<AudioInfo> audioFiles = new ArrayList<>();

        // Define the columns you want to retrieve
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE
        };

        // Query the external audio content URI
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long audioId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String audioPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String audioName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    long audioSizeInBytes = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                    // Convert bytes to megabytes
                    double audioSizeInMB = (audioSizeInBytes / (1024.0 * 1024.0));
                    double audioSizeInKB = (audioSizeInBytes / 1024.0);

                    AudioInfo audioInfo = new AudioInfo(audioId, audioPath, audioName, roundToTwoDecimals(audioSizeInKB));
                    audioFiles.add(audioInfo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return audioFiles;
    }

    public static Map<String, Integer> getAudioAlbumsWithCount(Context context) {
        Map<String, Integer> audioAlbumsWithCount = new HashMap<>();

        Cursor cursor = null;
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.Media.ALBUM};

            cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    MediaStore.Audio.Media.ALBUM + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    int songCount = getSongCountForAlbum(context, albumName);

                    audioAlbumsWithCount.put(albumName, songCount);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return audioAlbumsWithCount;
    }

    private static int getSongCountForAlbum(Context context, String albumName) {
        int songCount = 0;

        Cursor cursor = null;
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.Media._ID};
            String selection = MediaStore.Audio.Media.ALBUM + "=?";
            String[] selectionArgs = {albumName};

            cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    selection,
                    selectionArgs,
                    null);

            if (cursor != null) {
                songCount = cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return songCount;
    }

    public static void openMenuDialog(Activity activity, long id, String name, String path, String size, boolean isVideo, ImageView more, String isPresentInPlaylist) {

        myActivity = activity;

        PopupMenuCustomLayout popupMenu = new PopupMenuCustomLayout(activity, R.layout.menu_dialog,
                new PopupMenuCustomLayout.PopupMenuCustomOnClickListener() {
                    @Override
                    public void onClick(int itemId) {
                        // log statement: "Clicked on: " + itemId
                        switch (itemId) {
                            case R.id.play_menu:
                                // log statement: "Item A was clicked!"
                                break;
                            case R.id.add_to_playlist:
                                // log statement: "Item A was clicked!"
                                if (isVideo) {
                                    mainVideoInfoList = new ArrayList<>();
                                    mainVideoInfoList.add(new VideoInfo(id, name, Double.parseDouble(size), path));
                                    activity.startActivity(new Intent(activity, ResultActivity.class).putExtra("type", 11).putExtra("name", "Select Playlist"));
                                } else {
                                    mainAudioInfoList = new ArrayList<>();
                                    mainAudioInfoList.add(new AudioInfo(id, path, name, Double.parseDouble(size)));
                                    activity.startActivity(new Intent(activity, ResultActivity.class).putExtra("type", 12).putExtra("name", "Select Playlist"));
                                }
                                break;
                            case R.id.remove_from_playlist:
                                // log statement: "Item A was clicked!"
                                SharePreferences preferences = new SharePreferences(activity);
                                if (isVideo) {
                                    preferences.removeItemFromPlaylist(isPresentInPlaylist, path);
                                    activity.recreate();
                                } else {
                                    preferences.removeItemFromAudioPlaylist(isPresentInPlaylist, path);
                                    activity.recreate();
                                }
                                break;
                            case R.id.rename:
                                // log statement: "Item A was clicked!"
                                if (isStoragePermissionGranted(activity)) {
                                    renameDialog(activity, id, path, isVideo);
                                } else {
                                    requestStoragePermission(activity);
                                }
                                break;
                            case R.id.delete:
                                // log statement: "Item A was clicked!"
                                if (isStoragePermissionGranted(activity)) {
                                    List<Uri> uriList = new ArrayList<>();
                                    Uri uri;
                                    if (isVideo) {
                                        uri = ContentUris.withAppendedId(MediaStore.Video.Media.getContentUri("external"), id);
                                    } else {
                                        uri = ContentUris.withAppendedId(MediaStore.Audio.Media.getContentUri("external"), id);
                                    }
                                    uriList.add(uri);
                                    PendingIntent intent = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                        intent = MediaStore.createDeleteRequest(activity.getContentResolver(), uriList);
//                        MediaStore.createWriteRequest()
                                        IntentSender intentSender = intent.getIntentSender();
                                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(intentSender).build();

                                        try {
                                            mainOldFilePath = path;
                                            mainNewFileName = "";
                                            activity.startIntentSenderForResult(intent.getIntentSender(), 124, null, 0, 0, 0);
                                        } catch (IntentSender.SendIntentException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        deleteFile(path);
                                    }
                                    Log.e(TAG, "deleteFile: " + deleteFile(path));
                                } else {
                                    requestStoragePermission(activity);
                                }
                                break;
                            case R.id.share:
                                // log statement: "Item A was clicked!"
                                if (isStoragePermissionGranted(activity)) {
                                    if (isVideo) {
                                        shareVideoFile(activity, new File(path));
                                    } else {
                                        shareAudioFile(activity, new File(path));
                                    }
                                } else {
                                    requestStoragePermission(activity);
                                }
                                break;
                        }
                    }
                });
        if (isVideo) {
            TextView add = (TextView) popupMenu.getMenuItem(1);
            TextView remove = (TextView) popupMenu.getMenuItem(2);
            add.setCompoundDrawablesWithIntrinsicBounds(R.drawable.add_to_playlist, 0, 0, 0);
            remove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.remove_from_playlist, 0, 0, 0);
            if (!isPresentInPlaylist.isEmpty()) {
                add.setVisibility(View.GONE);
                remove.setVisibility(View.VISIBLE);
            } else {
                remove.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
            }
        } else {
            TextView add = (TextView) popupMenu.getMenuItem(1);
            TextView remove = (TextView) popupMenu.getMenuItem(2);
            add.setCompoundDrawablesWithIntrinsicBounds(R.drawable.add_music, 0, 0, 0);
            remove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.remove_music, 0, 0, 0);
            if (!isPresentInPlaylist.isEmpty()) {
                add.setVisibility(View.GONE);
                remove.setVisibility(View.VISIBLE);
            } else {
                remove.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
            }
        }
        popupMenu.show(more);

    }


    private static void renameDialog(Activity activity, long id, String path, boolean isVideo) {
        Dialog dialog = new Dialog(activity, R.style.SheetDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(activity);

        View lay = inflater.inflate(R.layout.dialog_playlist, null);
        ImageView closeBt = lay.findViewById(R.id.close);
        EditText nameEd = lay.findViewById(R.id.name_ed);
        TextView createBt = lay.findViewById(R.id.create_bt);
        TextView textView = lay.findViewById(R.id.tv_title);

        dialog.setContentView(lay);

        textView.setText(activity.getString(R.string.enter_new_name));
        nameEd.setHint(activity.getString(R.string.enter_new_name));
        createBt.setText(activity.getString(R.string.rename));

        createBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEd.getText().toString().isEmpty()) {
                    Toast.makeText(activity, activity.getString(R.string.enter_new_name), Toast.LENGTH_SHORT).show();
                } else {
                    List<Uri> uriList = new ArrayList<>();
                    Uri uri;
                    if (isVideo) {
                        uri = ContentUris.withAppendedId(MediaStore.Video.Media.getContentUri("external"), id);
                    } else {
                        uri = ContentUris.withAppendedId(MediaStore.Audio.Media.getContentUri("external"), id);
                    }
                    uriList.add(uri);
                    PendingIntent intent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        intent = MediaStore.createWriteRequest(activity.getContentResolver(), uriList);
//                        MediaStore.createWriteRequest()
                        IntentSender intentSender = intent.getIntentSender();
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(intentSender).build();

                        try {
                            mainOldFilePath = path;
                            mainNewFileName = nameEd.getText().toString().trim();
                            activity.startIntentSenderForResult(intent.getIntentSender(), 123, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Utils.renameAudioFile(path, nameEd.getText().toString().trim());
                    }
                    dialog.dismiss();
                }
            }
        });

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static Map<String, Integer> getArtistsWithSongCount(Context context) {
        Map<String, Integer> artistsWithSongCount = new HashMap<>();

        Cursor cursor = null;
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.Media.ARTIST};

            cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    MediaStore.Audio.Media.ARTIST + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    int songCount = getSongCountForArtist(context, artistName);

                    artistsWithSongCount.put(artistName, songCount);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return artistsWithSongCount;
    }

    private static int getSongCountForArtist(Context context, String artistName) {
        int songCount = 0;

        Cursor cursor = null;
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.Media._ID};
            String selection = MediaStore.Audio.Media.ARTIST + "=?";
            String[] selectionArgs = {artistName};

            cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    selection,
                    selectionArgs,
                    null);

            if (cursor != null) {
                songCount = cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return songCount;
    }

    public static List<VideoInfo> getVideoList(Context context) {
        List<VideoInfo> videoInfoList = new ArrayList<>();

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE
        };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder)) {

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long videoId = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    String videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    long videoSizeInBytes = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));

                    // Convert bytes to megabytes
                    double videoSizeInMB = (videoSizeInBytes / (1024.0 * 1024.0));

                    VideoInfo videoInfo = new VideoInfo(videoId, videoName, roundToTwoDecimals(videoSizeInMB), videoPath);
                    videoInfoList.add(videoInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videoInfoList;
    }

    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /*
        public static LiveData<List<String>> getAllVideosFromFolder(Context context, String folderPath) {
            MutableLiveData<List<String>> data = new MutableLiveData<>();
            List<String> videoPaths = new ArrayList<>();

            // Define the columns you want to retrieve
            String[] projection = {MediaStore.Video.Media.DATA};

            // Define the selection query
    //        String folderPath = "/storage/emulated/0/Download/All Video Downloader";

            String selection = MediaStore.Video.Media.DATA + " like ?";
            String[] selectionArgs = new String[]{"%" + folderPath + "%"};

            // Query the media store for videos matching the folder path
            Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);

            if (cursor != null) {

                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

                while (cursor.moveToNext()) {
                    String videoPath = cursor.getString(columnIndex);
                    videoPaths.add(videoPath);
                }

                cursor.close();
                AsyncTask.execute(() -> data.postValue(videoPaths));

            }

            return data;
        }
    */
    public static List<VideoInfo> getAllVideosFromFolder(Context context, String folderPath) {
        List<VideoInfo> videoInfoList = new ArrayList<>();

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA
        };

        String selection = MediaStore.Video.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%" + folderPath + "%"};

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

            while (cursor.moveToNext()) {

                long videoId = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String videoName = cursor.getString(nameIndex);
                long videoSizeInBytes = cursor.getLong(sizeIndex);
                String videoPath = cursor.getString(pathIndex);

                double videoSizeInMB = videoSizeInBytes / (1024.0 * 1024.0);

                VideoInfo videoInfo = new VideoInfo(videoId, videoName, roundToTwoDecimals(videoSizeInMB), videoPath);
                videoInfoList.add(videoInfo);
            }

            cursor.close();
        }

        return videoInfoList;
    }

    public static List<AudioInfo> getSongsFromAlbum(Context context, String albumName) {
        List<AudioInfo> audioInfoList = new ArrayList<>();

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATA
        };

        String selection = MediaStore.Audio.Media.ALBUM + "=?";
        String[] selectionArgs = new String[]{albumName};

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null) {
            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

            while (cursor.moveToNext()) {
                long songID = cursor.getLong(idIndex);
                String songName = cursor.getString(nameIndex);
                long songSizeInBytes = cursor.getLong(sizeIndex);
                String songPath = cursor.getString(pathIndex);

                double songSizeInMB = songSizeInBytes / (1024.0 * 1024.0);

                AudioInfo audioInfo = new AudioInfo(songID, songPath, songName, roundToTwoDecimals(songSizeInMB));
                audioInfoList.add(audioInfo);
            }

            cursor.close();
        }

        return audioInfoList;
    }

    public static List<AudioInfo> getSongsByArtist(Context context, String artistName) {
        List<AudioInfo> audioInfoList = new ArrayList<>();

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATA
        };

        String selection = MediaStore.Audio.Media.ARTIST + "=?";
        String[] selectionArgs = new String[]{artistName};

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null) {
            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

            while (cursor.moveToNext()) {
                long songId = cursor.getLong(idIndex);
                String songName = cursor.getString(nameIndex);
                long songSizeInBytes = cursor.getLong(sizeIndex);
                String songPath = cursor.getString(pathIndex);

                double songSizeInMB = songSizeInBytes / (1024.0 * 1024.0);

                AudioInfo audioInfo = new AudioInfo(idIndex, songPath, songName, roundToTwoDecimals(songSizeInMB));
                audioInfoList.add(audioInfo);
            }

            cursor.close();
        }

        return audioInfoList;
    }

/*    public static LiveData<List<String>> getAllImagesFromFolder() {
        MutableLiveData<List<String>> data = new MutableLiveData<>();
        List<String> imagePaths = new ArrayList<>();

        String folderPath = "/storage/emulated/0/Download/" + directoryInstaShoryDirectorydownload_images;
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file.getName())) {
                        String imagePath = file.getAbsolutePath();
                        imagePaths.add(imagePath);
                    }
                }
            }
        }

        String sfolder = "/storage/emulated/0" + SAVE_FOLDER_NAME;
        File files = new File(sfolder);
        if (files.exists() && files.isDirectory()) {
            File[] listFiles = files.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (file.isFile() && isImageFile(file.getName())) {
                        String imagePath = file.getAbsolutePath();
                        imagePaths.add(imagePath);
                    }
                }
            }
        }
        AsyncTask.execute(() -> data.postValue(imagePaths));

        return data;
    }*/

    private static boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName);
        return extension != null && (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("bmp"));
    }
/*
    public static ShimmerDrawable getShimmer() {
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setDuration(1200) // how long the shimmering animation takes to do one full sweep
                .setBaseAlpha(0.7f) //the alpha of the underlying children
                .setHighlightAlpha(0.9f) // the shimmer alpha amount
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT).setShape(Shimmer.Shape.LINEAR).setAutoStart(true).build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        return shimmerDrawable;
    }

    public static List<MyVideo> getAllVideosFromUri(Context context) {
        List<MyVideo> videoPaths = new ArrayList<>();

        String[] projection = {
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA};
        String sortOrder = MediaStore.Video.Media.DATE_MODIFIED + " DESC";

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            while (cursor.moveToNext()) {
                String videoPath = cursor.getString(columnIndex);
                String title = cursor.getString(titleIndex);
                videoPaths.add(new MyVideo(videoPath, title, Uri.fromFile(new File(videoPath))));
            }
            cursor.close();
            Log.e("getAllVideosFromUri: ", videoPaths.size() + " ");
        }

        return videoPaths;
    }*/

}
