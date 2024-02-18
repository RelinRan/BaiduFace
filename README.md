#### BaiduFace
百度人脸识别SDK，提炼出来的人脸识别和人脸注册。
#### [AAR]
[baidu_face.arr](https://github.com/RelinRan/BaiduFace/blob/master/baidu_face.aar)
```
android {
    ....
    repositories {
    flatDir {
            dirs 'libs'
        }
    }
}

dependencies {
    implementation(name: 'BaiduFace', ext: 'aar')
}

```
#### JitPack
项目/build.grade
```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
项目/app/build.grade
```
dependencies {
	    implementation 'com.github.RelinRan:BaiduFace:2024.2.18.1'
	}
```
#### 权限配置
```
<uses-feature
    android:name="android.hardware.camera.autofocus"
    android:required="true" />
<uses-feature
    android:name="android.hardware.camera"
    android:required="true" />
<uses-permission android:name="android.permission.CAMERA" />
```
#### AndroidManifest.xml
播放页面
```
<activity
    android:name=".xxx"
    android:configChanges="keyboardHidden|orientation|screenSize"></activity>
```
Application
```
android:usesCleartextTraffic="true"
```
文件操作
```
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileProvider"
    android:exported="false"
    android:grantUriPermissions="true"
    android:permission="android.permission.MANAGE_DOCUMENTS">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/path" />
    <intent-filter>
        <action android:name="android.content.action.DOCUMENTS_PROVIDER" />
    </intent-filter>
</provider>
```
path.xml
```
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <root-path
        name="root"
        path="/storage/emulated/0" />
    <files-path
        name="files"
        path="/storage/emulated/0/Android/data/${applicationId}/files" />
    <cache-path
        name="cache"
        path="/storage/emulated/0/Android/data/${applicationId}/cache" />
    <external-path
        name="external"
        path="/storage/emulated/0/Android/data/${applicationId}/external" />
    <external-files-path
        name="Capture"
        path="/storage/emulated/0/Android/data/${applicationId}/files/Capture" />
    <external-cache-path
        name="Pick"
        path="/storage/emulated/0/Android/data/${applicationId}/files/Pick" />
    <external-cache-path
        name="TBS"
        path="/storage/emulated/0/Android/data/${applicationId}/files/TBS" />
</paths>
```

#### xml布局
```
<com.baidu.idl.face.widget.FacePreview
    android:id="@+id/preview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
#### 参数配置
```
FaceSDK.initialize(getApplicationContext(), new SDKConfig("Your Project Name", "Your License Key"), null);
```
#### 播放视频
```
boolean register = true;
preview.setFaceMode(register ? FaceMode.REGISTER : FaceMode.RECOGNIZE);//设置人脸识别模式
//人脸跟踪监听
preview.setFaceTrackListener(new OnFaceTrackListener() {
    @Override
    public void onFaceTraceResult(TraceSearch traceSearch, TraceInfo info) {
        traceSearch.pause();//暂停人脸跟踪识别
        Bitmap bitmap = FaceImage.decodeByteArray(info.getImage());
        //保存人脸信息
        FaceInfo info = info.getFaceInfo();
        Log.i("FaceSDK", "人脸特征值：" + Hex.toHex(traceInfo.getFeature()));
        long index = database.addUser(info.faceID, traceInfo.getFeature(), etId.getText().toString(), etName.getText().toString(), traceInfo.getImage());
        Log.i("FaceSDK", "addUser index:" + index);
        if (index > -1) {
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        }
    }
});
//人脸搜索监听
preview.setFaceSearchListener(new OnFaceSearchListener() {
    @Override
    public void onFaceSearchResult(TraceSearch traceSearch, SearchInfo info) {
        traceSearch.pause();//暂停人脸跟踪识别
        String userId = info.getUserId();//用户ID
        String userName = info.getUserName();//用户姓名
        Bitmap bitmap = info.getBitmap();//用户头像
    }
});
```