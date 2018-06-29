ChoosePhoto
====
###1、[功能描述]：
#####主要用于头像修改、单张图片选择上传
###2、[项目结构简介]：
#####example为demo,里面主要包括使用方式，如何调用；choosephotoutil为依赖Module，主要功能都在choosephotoutil文件夹里，其他文件默认提交，不做说明
###3、[测试DEMO]：
#####调用选择图片代码如下
Intent intent = new Intent(MainActivity.this, DialogPhotoForCutActivity.class);<br>
                intent.putExtra("filePath", getPath(MainActivity.this));<br>
                startActivityForResult(intent, TAKE_PHOTO);<br>
#####获取图片路径回调代码如下
    @Override<br>
    public void onActivityResult(int requestCode, int resultCode, Intent data) {<br>
        if (resultCode == Activity.RESULT_OK) switch (requestCode) {<br>
            case TAKE_PHOTO:<br>
                if (null != data) {<br>
                    String imageName = data.getStringExtra("imageName");<br>
                    tv_path.setText(imageName);<br>
                    Bitmap bm = BitmapFactory.decodeFile(imageName);<br>
                    // 将图片显示到ImageView中<br>
                        avatar.setImageBitmap(bm);<br>
               }<br>
               break;<br>
        }<br>
        super.onActivityResult(requestCode, resultCode, data);<br>
    }<br>
###4、[历史版本]：
#####2018.6.28<br>
    *完成图片选择功能，其中包括有无切图功能，版本v1.0.0<br>
    *引用方式：   implementation 'com.github.githubmcj:choosephoto:v1.0.0'<br>

###5、[联系方式]：
##### email：550612711@qq.com 对这个工程不明白的地方可以通过该联系方式与我联系。
