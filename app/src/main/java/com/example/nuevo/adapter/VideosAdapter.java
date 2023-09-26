package com.example.nuevo.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nuevo.R;
import com.example.nuevo.VideoModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/** @noinspection ALL*/
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyHolder>    {


    private ArrayList<VideoModel> videoFolder = new ArrayList<>();
    private Context context;

    public VideosAdapter(ArrayList<VideoModel> videoFolder, Context context) {
        this.videoFolder = videoFolder;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.files_view, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        Glide.with(context).load(videoFolder.get(position).getPath()).into(holder.thumbnail);
        holder.title.setText(videoFolder.get(position).getTitle());
        holder.duration.setText(videoFolder.get(position).getDuration());
        holder.size.setText(videoFolder.get(position).getSize());
        holder.resolution.setText(videoFolder.get(position).getResolution());



    }

    private void shareFile(int position) {
    }


    private  void shareFile(){
        int p = 0;
        Uri uri = Uri.parse(videoFolder.get(p).getPath());
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent,"share"));
        Toast.makeText(context, "loading..", Toast.LENGTH_SHORT).show();
    }

    private void deleteFiles(int p, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("delete")
                .setMessage(videoFolder.get(p).getTitle())
                .setNegativeButton("cancel", (dialogInterface, i) -> {
                   //todo
                   // leave it as empty

                }).setPositiveButton("ok", (dialogInterface, i) -> {
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            Long.parseLong(videoFolder.get(p).getId()));
                    File file = new File(videoFolder.get(p).getPath());
                    boolean deleted = file.delete();
                    if(deleted){
                        context.getApplicationContext().getContentResolver().delete(contentUri,
                                null, null);
                        videoFolder.remove(p);
                        notifyItemRemoved(p);
                        notifyItemRangeChanged(p, videoFolder.size());
                        Snackbar.make(view, "file Deleted Success", Snackbar.LENGTH_SHORT).show();

                    }else {
                        Snackbar.make(view, "file Deleted fail", Snackbar.LENGTH_SHORT).show();
                    }

                }).show();
    }
    private void renameFiles(int position, View view){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.rename_layout);
        EditText editText = dialog.findViewById(R.id.rename_edit_text);
        Button cancel = dialog.findViewById(R.id.cancel_rename_button);
        Button rename_btn = dialog.findViewById(R.id.rename_button);
        final File renameFile = new File(videoFolder.get(position).getPath());
        String nameText = renameFile.getName();
        nameText = nameText.substring(0, nameText.lastIndexOf("."));
        editText.setText(nameText);
        editText.clearFocus();
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        cancel.setOnClickListener(v-> dialog.dismiss());
        rename_btn.setOnClickListener(v1->{
            String onlyPath = Objects.requireNonNull(renameFile.getParentFile()).getAbsolutePath();
            String ext = renameFile.getAbsolutePath();
            ext = ext.substring(ext.lastIndexOf("."));
            String newPath = onlyPath + "/" + editText.getText() + ext;
            File newFile = new File(newPath);
            boolean rename = renameFile.renameTo(newFile);
            if(rename){
                context.getApplicationContext().getContentResolver()
                        .delete(MediaStore.Files.getContentUri("external"),
                                MediaStore.MediaColumns.DATA + "=?",
                                new String[]{renameFile.getAbsolutePath()});
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(newFile));
                context.getApplicationContext().sendBroadcast(intent);
                Snackbar.make(view, "Rename Success", Snackbar.LENGTH_SHORT).show();
                Snackbar.make(view, "Rename Failed", Snackbar.LENGTH_SHORT).show();

            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showProperties(int p){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.file_properties);

        String name = videoFolder.get(p).getTitle();
        String path = videoFolder.get(p).getPath();
        String size = videoFolder.get(p).getSize();
        String resolution = videoFolder.get(p).getResolution();
        String duration = videoFolder.get(p).getDuration();

        TextView tit = dialog.findViewById(R.id.pro_title);
        TextView st = dialog.findViewById(R.id.pro_storage);
        TextView siz = dialog.findViewById(R.id.pro_size);
        TextView dur = dialog.findViewById(R.id.pro_duration);
        TextView res = dialog.findViewById(R.id.pro_resolution);

        tit.setText(name);
        st.setText(path);
        siz.setText(size);
        dur.setText(duration);
        res.setText(resolution + "p");

        dialog.show();

    }
    @Override
    public int getItemCount() {
        return videoFolder.size();
    }


    /** @noinspection InnerClassMayBeStatic*/
    public static class MyHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, menu;
        TextView title, size, duration, resolution;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.video_title);
            size = itemView.findViewById(R.id.video_size);
            duration = itemView.findViewById(R.id.video_duration);
            resolution = itemView.findViewById(R.id.video_quality);
            menu = itemView.findViewById(R.id.video_menu);


        }
    }
}
