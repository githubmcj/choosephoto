package com.photo.util.choosephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.photo.util.choosephotoutil.DialogPhotoForCutActivity;
import com.photo.util.choosephotoutil.DialogPhotoForNoCutActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView avatar, avatar_no_cut;
    private TextView tv_path;
    private static final int TAKE_PHOTO = 1; // 拍照
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        avatar = (ImageView) findViewById(R.id.avatar);
        avatar_no_cut = (ImageView) findViewById(R.id.avatar_no_cut);
        tv_path = (TextView) findViewById(R.id.tv_path);
        verifyStoragePermissions(this);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 0;
                Intent intent = new Intent(MainActivity.this, DialogPhotoForCutActivity.class);
                intent.putExtra("filePath", getPath(MainActivity.this));
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        avatar_no_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                Intent intent = new Intent(MainActivity.this, DialogPhotoForNoCutActivity.class);
                intent.putExtra("filePath", getPath(MainActivity.this));
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
    }

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    /**
     * 获取文件访问权限，6.0以上需要添加
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        //获取读写SD卡权限
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPath(Context context) {
        String path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//判断sd卡是否存在,获取sdk的存储路径
            path = Environment.getExternalStorageDirectory().toString();
        } else {//获取内部存储路径
            path = context.getFilesDir().getAbsolutePath();
        }
        return path;
    }

   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) switch (requestCode) {
            case TAKE_PHOTO:
                if (null != data) {
                    String imageName = data.getStringExtra("imageName");
                    tv_path.setText(imageName);
                    Bitmap bm = BitmapFactory.decodeFile(imageName);
                    // 将图片显示到ImageView中
                    if (type == 0) {
                        avatar.setImageBitmap(bm);
                    } else {
                        avatar_no_cut.setImageBitmap(bm);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
