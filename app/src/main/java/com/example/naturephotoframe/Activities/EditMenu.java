package com.example.naturephotoframe.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.example.naturephotoframe.BackgrounEraser.HoverView;
import com.example.naturephotoframe.BackgrounEraser.MyCustomView;
import com.example.naturephotoframe.R;
import com.example.naturephotoframe.Utils.BitmapUtils;
import com.example.naturephotoframe.Utils.Common;

import java.io.File;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class EditMenu extends Activity implements View.OnClickListener {

    ImageView checked;
    private String imagePath;
    private Intent intent;
    private ContentResolver mContentResolver;

    HoverView mHoverView;
    double mDensity;

    int viewWidth;
    int viewHeight;
    int bmWidth;
    int bmHeight;

    int actionBarHeight;
    int bottombarHeight;
    double bmRatio;
    double viewRatio;

    Button    positionButton;
    ImageButton eraserMainButton,magicWandMainButton,mirrorButton;
    ImageView eraserSubButton, unEraserSubButton;
    ImageView brushSize1Button, brushSize2Button, brushSize3Button, brushSize4Button;
    ImageView magicRemoveButton, magicRestoreButton;
    ImageView  undoButton, redoButton;
    Button nextButton;
    ImageView colorButton;

    SeekBar magicSeekbar;
    RelativeLayout eraserLayout, magicLayout;
    RelativeLayout mLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
        mContentResolver = getContentResolver();

        if (Build.VERSION.SDK_INT >= 23 ) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }



        mLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mDensity = getResources().getDisplayMetrics().density;
        actionBarHeight = (int)(110*mDensity);
        bottombarHeight = (int)(60*mDensity);


        viewWidth = getResources().getDisplayMetrics().widthPixels;
        viewHeight = getResources().getDisplayMetrics().heightPixels - actionBarHeight - bottombarHeight;
        viewRatio = (double) viewHeight/ (double) viewWidth;

        bmRatio = (double) Common.cameraBitmap.getHeight()/ (double) Common.cameraBitmap.getWidth();
        if(bmRatio < viewRatio) {
            bmWidth = viewWidth;
            bmHeight = (int)(((double)viewWidth)*((double)(Common.cameraBitmap.getHeight())/(double)(Common.cameraBitmap.getWidth())));
        } else {
            bmHeight = viewHeight;
            bmWidth = (int)(((double)viewHeight)*((double)(Common.cameraBitmap.getWidth())/(double)(Common.cameraBitmap.getHeight())));
        }


        Common.cameraBitmap = Bitmap.createScaledBitmap(Common.cameraBitmap, bmWidth, bmHeight, false);

        mHoverView = new HoverView(this, Common.cameraBitmap, bmWidth, bmHeight, viewWidth, viewHeight);
        mHoverView.setLayoutParams(new ViewGroup.LayoutParams(viewWidth, viewHeight));

        mLayout.addView(mHoverView);

        initButton();


    }

    public void updateRedoButton() {
        if(mHoverView.checkRedoEnable()) {
            redoButton.setEnabled(true);
            redoButton.setAlpha(1.0f);
        }
        else {
            redoButton.setEnabled(false);
            redoButton.setAlpha(0.3f);
        }
    }
    public void updateUndoButton() {
        if(mHoverView.checkUndoEnable()) {
            undoButton.setEnabled(true);
            undoButton.setAlpha(1.0f);
        }
        else {
            undoButton.setEnabled(false);
            undoButton.setAlpha(0.3f);
        }
    }

    public void resetMainButtonState() {
        eraserMainButton.setSelected(false);
        magicWandMainButton.setSelected(false);
        mirrorButton.setSelected(false);

    }

    public void resetSubEraserButtonState() {
        eraserSubButton.setSelected(false);
        unEraserSubButton.setSelected(false);
    }

    public void resetSubMagicButtonState() {
        magicRemoveButton.setSelected(false);
        magicRestoreButton.setSelected(false);
    }

    public void resetBrushButtonState() {
        brushSize1Button.setSelected(false);
        brushSize2Button.setSelected(false);
        brushSize3Button.setSelected(false);
        brushSize4Button.setSelected(false);
    }
    public void resetSeekBar() {
        magicSeekbar.setProgress(0);
        mHoverView.setMagicThreshold(0);
    }

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    private Matrix getOrientationMatrix(String path) {
        Matrix matrix = new Matrix();
        ExifInterface exif;
        try {
            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return matrix;
    }

    int currentColor = 0;
    public void setBackGroundColor(int color) {
        switch (color) {
            case 0:
                mLayout.setBackgroundResource(R.drawable.bg);
                colorButton.setBackgroundResource(R.drawable.white_drawable);
                break;
            case 1:
                mLayout.setBackgroundColor(Color.WHITE);
                colorButton.setBackgroundResource(R.drawable.black_drawable);
                break;
            case 2:
                mLayout.setBackgroundColor(Color.BLACK);
                colorButton.setBackgroundResource(R.drawable.transparent_drawable);
                break;

            default:
                break;
        }

        currentColor = color;
    }


    @Override
    public void onClick(View v) {
        updateUndoButton();
        updateRedoButton();

        switch (v.getId()) {
            case R.id.eraseButton:
                mHoverView.switchMode(mHoverView.ERASE_MODE);
                if(eraserLayout.getVisibility() == View.VISIBLE) {
                    eraserLayout.setVisibility(View.GONE);
                } else {
                    eraserLayout.setVisibility(View.VISIBLE);
                }
                magicLayout.setVisibility(View.GONE);
                resetMainButtonState();
                resetSubEraserButtonState();
                eraserSubButton.setSelected(true);
                eraserMainButton.setSelected(true);
                break;
            case R.id.magicButton:
                mHoverView.switchMode(HoverView.MAGIC_MODE);
                if(magicLayout.getVisibility() == View.VISIBLE) {
                    magicLayout.setVisibility(View.GONE);
                } else {
                    magicLayout.setVisibility(View.VISIBLE);
                }
                eraserLayout.setVisibility(View.GONE);
                resetMainButtonState();
                resetSubMagicButtonState();
                resetSeekBar();
                magicRemoveButton.setSelected(true);
                magicWandMainButton.setSelected(true);

                break;

            case R.id.mirrorButton:
                findViewById(R.id.eraser_layout).setVisibility(View.GONE);
                findViewById(R.id.magicWand_layout).setVisibility(View.GONE);
                mHoverView.mirrorImage();
                break;

            case R.id.erase_sub_button:
                mHoverView.switchMode(HoverView.ERASE_MODE);
                resetSubEraserButtonState();
                eraserSubButton.setSelected(true);
                break;
            case R.id.unerase_sub_button:
                mHoverView.switchMode(HoverView.UNERASE_MODE);
                resetSubEraserButtonState();
                unEraserSubButton.setSelected(true);
                break;

            case R.id.brush_size_1_button:
                resetBrushButtonState();
                mHoverView.setEraseOffset(40);
                brushSize1Button.setSelected(true);
                break;

            case R.id.brush_size_2_button:
                resetBrushButtonState();
                mHoverView.setEraseOffset(60);
                brushSize2Button.setSelected(true);
                break;

            case R.id.brush_size_3_button:
                resetBrushButtonState();
                mHoverView.setEraseOffset(80);
                brushSize3Button.setSelected(true);
                break;

            case R.id.brush_size_4_button:
                resetBrushButtonState();
                mHoverView.setEraseOffset(100);
                brushSize4Button.setSelected(true);
                break;

            case R.id.magic_remove_button:
                resetSubMagicButtonState();
                magicRemoveButton.setSelected(true);
                mHoverView.switchMode(HoverView.MAGIC_MODE);
                resetSeekBar();
                break;

            case R.id.magic_restore_button:
                resetSubMagicButtonState();
                magicRestoreButton.setSelected(true);
                mHoverView.switchMode(HoverView.MAGIC_MODE_RESTORE);
                resetSeekBar();
                break;

            case R.id.checked:
                Intent intent = new Intent(getApplicationContext(), MyWorkSpace.class);
                intent.putExtra("imagePath", mHoverView.save());
                startActivity(intent);
                break;

            case R.id.colorButton:
                setBackGroundColor((currentColor+1)%3);
                break;

            case R.id.undoButton:
                findViewById(R.id.eraser_layout).setVisibility(View.GONE);
                findViewById(R.id.magicWand_layout).setVisibility(View.GONE);
                mHoverView.undo();
                if(mHoverView.checkUndoEnable()) {
                    undoButton.setEnabled(true);
                    undoButton.setAlpha(1.0f);
                }
                else {
                    undoButton.setEnabled(false);
                    undoButton.setAlpha(0.3f);
                }
                updateRedoButton();
                break;
            case R.id.redoButton:
                findViewById(R.id.eraser_layout).setVisibility(View.GONE);
                findViewById(R.id.magicWand_layout).setVisibility(View.GONE);
                mHoverView.redo();
                updateUndoButton();
                updateRedoButton();
                break;
        }

    }

    public void initButton() {
        checked = findViewById(R.id.checked);
        checked.setOnClickListener(this);
        eraserMainButton = findViewById(R.id.eraseButton);
        eraserMainButton.setOnClickListener(this);
        magicWandMainButton = findViewById(R.id.magicButton);
        magicWandMainButton.setOnClickListener(this);
        mirrorButton = findViewById(R.id.mirrorButton);
        mirrorButton.setOnClickListener(this);


        eraserSubButton = (ImageView) findViewById(R.id.erase_sub_button);
        eraserSubButton.setOnClickListener(this);
        unEraserSubButton = (ImageView) findViewById(R.id.unerase_sub_button);
        unEraserSubButton.setOnClickListener(this);

        brushSize1Button = (ImageView) findViewById(R.id.brush_size_1_button);
        brushSize1Button.setOnClickListener(this);

        brushSize2Button = (ImageView) findViewById(R.id.brush_size_2_button);
        brushSize2Button.setOnClickListener(this);

        brushSize3Button = (ImageView) findViewById(R.id.brush_size_3_button);
        brushSize3Button.setOnClickListener(this);

        brushSize4Button = (ImageView) findViewById(R.id.brush_size_4_button);
        brushSize4Button.setOnClickListener(this);


        magicSeekbar = (SeekBar) findViewById(R.id.magic_seekbar);
        magicSeekbar.setProgress(15);
        magicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHoverView.setMagicThreshold(seekBar.getProgress());
                if(mHoverView.getMode() == mHoverView.MAGIC_MODE)
                    mHoverView.magicEraseBitmap();
                else if(mHoverView.getMode() == mHoverView.MAGIC_MODE_RESTORE)
                    mHoverView.magicRestoreBitmap();
                mHoverView.invalidateView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				
            }
        });

        magicRemoveButton = (ImageView) findViewById(R.id.magic_remove_button);
        magicRemoveButton.setOnClickListener(this);
        magicRestoreButton = (ImageView) findViewById(R.id.magic_restore_button);
        magicRestoreButton.setOnClickListener(this);

        undoButton = (ImageView) findViewById(R.id.undoButton);
        undoButton.setOnClickListener(this);

        redoButton = (ImageView) findViewById(R.id.redoButton);
        redoButton.setOnClickListener(this);
        updateRedoButton();

        eraserLayout = (RelativeLayout) findViewById(R.id.eraser_layout);
        magicLayout = (RelativeLayout) findViewById(R.id.magicWand_layout);
        eraserMainButton.setSelected(true);

        colorButton = (ImageView) findViewById(R.id.colorButton);
        colorButton.setOnClickListener(this);
    }

}
