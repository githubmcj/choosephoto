package com.photo.util.choosephotoutil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SdCardPath")
public class DialogPhotoForCutActivity extends Activity{
    DialogPhotoForCutActivity context;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int LOAD_PICTURE_KITKAK = 4;// 4.4版本（小米手处理问题）
    private static final int LOAD_PICTURE = 5;// 4.4一下版本（小米手处理问题）
    private Button button_cancle;// 取消按钮
    private static int screenHeight;
    private RelativeLayout rl_photobycamer;
    private RelativeLayout rl_photobygallery;
    private String imageName;
    Intent intent;
    private String my_path;//剪切图片保存路径
    private String filePath;//图片存储路径，由头像编辑页面传递过来

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Activity标题不显示
        this.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                LayoutParams.FLAG_FULLSCREEN);// 设置全屏显示
        setContentView(R.layout.activity_reward_apply_photo);
        context = this;
        initView();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    @SuppressWarnings("deprecation")
    private void initView() {
        // TODO Auto-generated method stub
        screenHeight = getWindow().getWindowManager().getDefaultDisplay()
                .getHeight();// 获取屏幕高度
        LayoutParams lp = getWindow().getAttributes();// lp包含了布局的很多信息，通过lp来设置对话框的布局
        lp.width = LayoutParams.FILL_PARENT;
        lp.gravity = Gravity.BOTTOM;
        lp.height = screenHeight / 3;// lp高度设置为屏幕的一半(改为1/4)
        getWindow().setAttributes(lp);// 将设置好属性的lp应用到对话框
        button_cancle = (Button) findViewById(R.id.button_cancle);
        rl_photobycamer = (RelativeLayout) findViewById(R.id.rl_photobycamer);
        rl_photobygallery = (RelativeLayout) findViewById(R.id.rl_photobygallery);
        // 取消按钮的点击事件监听
        button_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rl_photobycamer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                imageName = getNowTime() + ".jpeg";
                if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                    Uri photoURI = FileProvider.getUriForFile(DialogPhotoForCutActivity.this, "com.photo.util.choosephotoutil.fileprovider", new File(filePath + "/photo/", imageName));
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);//将拍取的照片保存到指定URI
                    startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                } else {
                    // 调用拍照 (参数 ：MediaStore.ACTION_IMAGE_CAPTURE)
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 指定调用相机拍照后照片的储存路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(filePath + "/photo/", imageName)));
                    startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                }
            }
        });
        rl_photobygallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                imageName = getNowTime() + ".jpeg";
                // 从相册获取照片(参数 ：Intent.ACTION_PICK)
                intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    startActivityForResult(intent, LOAD_PICTURE_KITKAK);//4.4版本
                } else {
                    startActivityForResult(intent, LOAD_PICTURE);//4.4以下版本，先不处理
                }
            }
        });
        button_cancle.setHeight(lp.height / 6);// 将button的高度设置为对话框的1/6
        if(getIntent() != null){
            filePath = getIntent().getStringExtra("filePath");
            initFile(filePath);
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        context.overridePendingTransition(0, R.anim.dialog_photo_exit);
    }

    /**
     * 获取当前系统时间作为文件名
     *
     * @return
     */
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    private void initFile(String filePath) {
        File dir = new File(filePath + "/photo/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case PHOTO_REQUEST_GALLERY:
                    if (null != data) {
                        startPhotoZoom(data.getData(), 600);
                    }
                    break;

                case LOAD_PICTURE://4.4以下版本
                    if (null != data) {
                        startPhotoZoom(data.getData(), 600);
                    }
                    break;

                case LOAD_PICTURE_KITKAK://4.4以上版本
                    startPhotoZoom(data.getData(), 600);
//					Uri selectedImage = data.getData();
//					String picturePath = ImageUtil.getPath(this, selectedImage);
//					Intent resultIntent3 = new Intent();
//					resultIntent3.putExtra("imageName", picturePath);
//					this.setResult(RESULT_OK, resultIntent3);
//					this.finish();
                    break;

                case PHOTO_REQUEST_TAKEPHOTO:
                    startPhotoZoom(Uri.fromFile(new File(filePath + "/photo/", imageName)), 600);
                    break;

                case PHOTO_REQUEST_CUT:
                    Intent resultIntent = new Intent();
                    if(my_path.equals("")){
                        resultIntent.putExtra("imageName", filePath + "/photo/" + imageName);
                    } else {
                        resultIntent.putExtra("imageName", my_path);
                    }
                    this.setResult(RESULT_OK, resultIntent);
                    this.finish();
                    break;

                default:
                    break;
            }
        }
    }

    @SuppressLint("SdCardPath")
    private void startPhotoZoom(Uri data, int size) {
        my_path = "";
        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
            my_path = getRealPathFromURI(DialogPhotoForCutActivity.this, data);
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(Uri.fromFile(new File(my_path)), "image/*");
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", "true");

            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);

            // outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputX", size);
            intent.putExtra("outputY", size);
            intent.putExtra("return-data", false);
            intent.putExtra("scale", true);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(my_path)));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            startActivityForResult(intent, PHOTO_REQUEST_CUT);
        } else {
            my_path = getRealPathFromURI(DialogPhotoForCutActivity.this, data);
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(data, "image/*");
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", "true");

            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);

            // outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputX", size);
            intent.putExtra("outputY", size);
            intent.putExtra("return-data", false);
            intent.putExtra("scale", true);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, data);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            startActivityForResult(intent, PHOTO_REQUEST_CUT);
        }
    }


    private String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,
                new String[]{MediaStore.Images.ImageColumns.DATA},//
                null, null, null);
        if (cursor == null) result = contentURI.getPath();
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }


}
