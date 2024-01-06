package com.example.naturephotoframe.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.WorkSource;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divyanshu.colorseekbar.ColorSeekBar;
import com.example.naturephotoframe.Adapter.FiltersAdapter;
import com.example.naturephotoframe.Adapter.FramesAdapter;
import com.example.naturephotoframe.Adapter.StickerAdapter;
import com.example.naturephotoframe.BackgrounEraser.TryonView;
import com.example.naturephotoframe.Model.Frames;
import com.example.naturephotoframe.R;
import com.example.naturephotoframe.Utils.BitmapUtils;
import com.example.naturephotoframe.Utils.Common;
import com.jsibbold.zoomage.ZoomageView;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MyWorkSpace extends AppCompatActivity implements FiltersAdapter.ThumbnailsAdapterListener {



    public static PhotoEditorView mImage;
    //ZoomageView zoomableImage;
    private String imagePath;
    private Intent intent;
    RecyclerView recyclerView;
    public ImageView mainPic;
    private ContentResolver mContentResolver;
    private Bitmap headBitmap;
    private Bitmap bodyBitmap;
    public static ImageView framesImage;
    ImageButton filterBtn, stickers, background, text;
    List<ThumbnailItem> thumbnailItemList;
    FiltersAdapter filtersAdapter;
    MyWorkSpaceListener myWorkSpaceListener;
    static String IMAGE_NAME = "placeholder.jpg";
    Bitmap originalImage;
    int viewWidth;
    int viewheight;

    ImageView bodyImageView;
    TryonView mTryOnView;
    Bitmap filteredImage;
    int localColor = R.color.teal_200;
    Bitmap finalImage;
    ArrayList<Frames> frameList = new ArrayList<>();
    ArrayList<Frames> stickersList = new ArrayList<>();
    FramesAdapter framesAdapter;
    StickerAdapter stickerAdapter;
    ArrayList<Frames> backgroundList = new ArrayList<>();
    PhotoEditor mPhotoEditor;


    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_work_space);
        hooks();
        mContentResolver = getContentResolver();
        intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");
        Log.i("headBitmap", "onCreate: " + imagePath);
        headBitmap = getBitmap(imagePath, viewWidth * 2 / 3);
        loadImage();


        frameList.add(new Frames(R.drawable.frame1));
        frameList.add(new Frames(R.drawable.frame13));


        bgList();
        stickerList();

        mPhotoEditor = new PhotoEditor.Builder(this, mImage)
                .setPinchTextScalable(true)
                .build();


        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterRecyclerView();
            }
        });

        stickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stikersRecyclerView();
            }
        });
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundRecyclerView();
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        mPhotoEditor.addImage(Common.cameraBitmap);

    }

    public void showDialog() {

        final int[] colorposition = new int[1];

        final String[] styles = new String[]{
                "theano_didot_regular.ttf",
                "dollie_script_personal_use.ttf"

        };
        LayoutInflater inflater;
        View view;
        inflater = LayoutInflater.from(this);
        view = inflater.inflate(R.layout.text_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        Button yes = view.findViewById(R.id.done);
        Button no = view.findViewById(R.id.cancel);
        Button font = view.findViewById(R.id.font);
        Button btncolors = view.findViewById(R.id.colorbutton);
        EditText etxt = view.findViewById(R.id.edittext);
        ColorSeekBar colorSeekBar = view.findViewById(R.id.colorseekbar);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                final String[] style = new String[]{
                        "theano_didot_regular.ttf",
                        "dollie_script_personal_use.ttf"
                };
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyWorkSpace.this,
                        android.R.layout.simple_spinner_item, style);
                spinner.setAdapter(adapter);

            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                String text = etxt.getText().toString();
                if (etxt.getText().toString().isEmpty()) {
                    etxt.setError("Please provide text...");
                    etxt.requestFocus();
                } else {
                    etxt.getText().clear();
                    alertDialog.dismiss();
                }

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btncolors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeekBar.setVisibility(View.VISIBLE);
            }
        });
        alertDialog.show();
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                localColor = i;
                etxt.setTextColor(i);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                colorposition[0] = i;
                etxt.setTypeface(Typeface.createFromAsset(MyWorkSpace.this.getAssets(), styles[colorposition[0]]));
                mPhotoEditor.addText(Typeface.createFromAsset(MyWorkSpace.this.getAssets(), styles[colorposition[0]]), etxt.getText().toString(), localColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public void stickerList() {
        stickersList.add(new Frames(R.drawable.sticker1));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker1));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker1));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker1));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker1));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker1));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
        stickersList.add(new Frames(R.drawable.sticker11));
    }

    public void bgList() {
        backgroundList.add(new Frames(R.drawable.bg_one));
        backgroundList.add(new Frames(R.drawable.bg_two));
        backgroundList.add(new Frames(R.drawable.bg_three));
        backgroundList.add(new Frames(R.drawable.bg_four));
        backgroundList.add(new Frames(R.drawable.bg_five));
        backgroundList.add(new Frames(R.drawable.bg_six));
        backgroundList.add(new Frames(R.drawable.bg_seven));
        backgroundList.add(new Frames(R.drawable.bg_eight));
        backgroundList.add(new Frames(R.drawable.bg_nine));
        backgroundList.add(new Frames(R.drawable.bg_ten));
        backgroundList.add(new Frames(R.drawable.bg_eleven));
        backgroundList.add(new Frames(R.drawable.bg_teweleve));
        backgroundList.add(new Frames(R.drawable.bg_thirteen));
        backgroundList.add(new Frames(R.drawable.bg_fourteen));
        backgroundList.add(new Frames(R.drawable.bg_fifteen));
        backgroundList.add(new Frames(R.drawable.bg_sixteen));
        backgroundList.add(new Frames(R.drawable.bg_seventeen));
        backgroundList.add(new Frames(R.drawable.bg_eighteen));
        backgroundList.add(new Frames(R.drawable.bg_ninteen));
        backgroundList.add(new Frames(R.drawable.bg_twenty));
        backgroundList.add(new Frames(R.drawable.bg_twentyone));
        backgroundList.add(new Frames(R.drawable.bg_twenty_two));
        backgroundList.add(new Frames(R.drawable.bg_twenty_three));
        backgroundList.add(new Frames(R.drawable.bg_twenty_four));
        backgroundList.add(new Frames(R.drawable.bg_twenty_five));
    }

    private void stikersRecyclerView() {
        stickerAdapter = new StickerAdapter(stickersList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(stickerAdapter);
    }

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    private Bitmap getBitmap(String path, int maxWidth) {
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = maxWidth;
            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math
                        .round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void hooks() {
        mImage = findViewById(R.id.photoEditorView);
        text = findViewById(R.id.text);
        recyclerView = findViewById(R.id.recyclerView);
        filterBtn = findViewById(R.id.filters);
        stickers = findViewById(R.id.stickers);
        background = findViewById(R.id.background);
        framesImage = findViewById(R.id.framesImage);
    }

    // load the default image from assets on app launch
    private void loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        mImage.getSource().setImageBitmap(headBitmap);

    }


    public void backgroundRecyclerView() {
        framesAdapter = new FramesAdapter(backgroundList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(framesAdapter);

    }

    public void filterRecyclerView() {
        thumbnailItemList = new ArrayList<>();
        filtersAdapter = new FiltersAdapter(getApplicationContext(), thumbnailItemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(filtersAdapter);
        prepareThumbnail(headBitmap);

    }

    public void prepareThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage;

                if (bitmap == null) {
                    thumbImage = BitmapUtils.getBitmapFromAssets(getApplicationContext(), IMAGE_NAME, 100, 100);
                } else {
                    thumbImage = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                }

                if (thumbImage == null)
                    return;

                ThumbnailsManager.clearThumbs();
                thumbnailItemList.clear();

                // add normal bitmap first
                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImage;
                thumbnailItem.filterName = getString(R.string.filter_normal);
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters = FilterPack.getFilterPack(MyWorkSpace.this);

                for (Filter filter : filters) {
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbImage;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }

                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(MyWorkSpace.this));

                MyWorkSpace.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        filtersAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        new Thread(r).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // clear bitmap memory
        originalImage.recycle();
        finalImage.recycle();
        finalImage.recycle();

        originalImage = Common.cameraBitmap.copy(Bitmap.Config.ARGB_8888, true);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        mImage.getSource().setImageBitmap(originalImage);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        if (myWorkSpaceListener != null)
            myWorkSpaceListener.onFilterSelected(filter);
        // reset image controls
        originalImage = Common.cameraBitmap.copy(Bitmap.Config.ARGB_8888, true);
        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        mImage.getSource().setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    public void setListener(MyWorkSpaceListener listener) {
        this.myWorkSpaceListener = listener;
    }

    public interface MyWorkSpaceListener {
        void onFilterSelected(Filter filter);
    }
}