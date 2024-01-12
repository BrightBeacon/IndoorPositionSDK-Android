# IndoorPositionSDK-Android
### 项目介绍

IndoorPositionSDK for Android



### 使用说明

#### 一、添加室内地图库SDK库

复制以下SDK Lib文件到目录工程 app/libs 目录下：

```java
BRTMap3DLibrary-release-2.3.1.aar		// 	室内地图库
MapboxGLAndroidSDK-release-9.1.1.aar	//	室内地图核心引擎库  
BRTMapData-release-2.3.1.aar			// 	室内地图及定位基础库    

// 可选库
BRTLocationLibrary-release-2.3.1.aar	//	室内定位库（无室内定位需求，可忽略）
```



#### 二、修改工程配置

##### 打开工程目录下 app/build.gradle 文件

在 android {} 代码块中检查是否有以下配置项，如果没有请添加：

```java
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}		
```

在 dependencies {} 代码块中添加以下代码和地图库引用配置项

```java
implementation fileTree(dir: "libs", include: ["*.aar"])

//  地图核心引擎依赖库
implementation group: 'com.mapbox.mapboxsdk', name: 'mapbox-android-telemetry', version: '5.0.0'
implementation group: 'com.mapbox.mapboxsdk', name: 'mapbox-sdk-geojson', version: '5.0.0'
implementation group: 'com.mapbox.mapboxsdk', name: 'mapbox-android-gestures', version: '0.6.0'
implementation group: 'com.mapbox.mapboxsdk', name: 'mapbox-sdk-turf', version: '5.0.0'

//  室内路径规划及导航依赖库
implementation 'com.vividsolutions:jts:1.13'
```



#### 三、添加权限声明及运行时权限申请

##### 1、打开工程 AndroidManifest.xml 文件声明相关权限

添加以下运行时权限声明


```xml
<!-- 用于在线校验智石地图SDK开发密钥, 获取在线地图数据和在线路径规划数据 -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- 用于室内地图相关数据的保存和加载 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />


<!-- 以下权限可选，需要室内定位功能请添加相应声明 -->

<!-- 蓝牙扫描权限,用于扫描蓝牙Beacon定位信标. -->
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />

<!-- 安卓6.0及以后系统需要声明以下权限，才能扫描到蓝牙Beacon信标 -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- 如果APP编译targetSdk>=31(即安卓12及以上版本),需声明以下权限 -->
<!-- 蓝牙扫描权限 -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<!-- 蓝牙连接权限，控制蓝牙开关 (该权限可选，方便用户开启蓝牙功能) -->
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

蓝牙扫描Beacon接口对权限的要求，请参考以下安卓开发文档：

https://developer.android.google.cn/reference/android/bluetooth/le/BluetoothLeScanner



##### 2、添加运行时权限申请

安卓6.0以上系统，需要为APP添加相应的运行时权限申请代码：

```java
private static final int REQUST_PERMISSION_CODE = 1000;

private void checkPermissions() {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		List<String> permissions = new ArrayList<>();
		// 室内地图数据的保存和读取相关权限
		permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
		
		// 需要定位权限才能正常扫描蓝牙Beacon定位信标
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        
		// 如果APP编译targetSdk>=31(即安卓12及以上版本)需要运行时申请以下权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // 蓝牙扫描权限
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            // 蓝牙连接权限控制蓝牙开关
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
		requestPermissions(permissions.toArray(new String[0]), REQUST_PERMISSION_CODE);
	}
}
```



#### 四、编写地图加载显示代码

以下准备在 MainActivity.java 中展示室内地图, 其对应的ContentView资源文件为: activity_main.xml

##### 1、修改activity_main.xml

添加BRTMapView 标签, 并设置其ID为 mapView

```xml
<com.brtbeacon.map.map3d.BRTMapView
    android:id="@+id/mapView"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
</com.brtbeacon.map.map3d.BRTMapView>
```

##### 2、修改MainActivity.java

1. 声明室内地图对象

   ```java
   private BRTMapView mapView;
   ```

2. 修改onCreate方法

   ```java
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
   	// 初始化地图引擎环境(必须在setContentView之前调用)
       BRTMapEnvironment.initMapEnvironment(this);
       setContentView(R.layout.activity_main);
       mapView = findViewById(R.id.mapView);
       mapView.addMapListener(mapViewListener);
       // 检查并申请权限
       if(!checkNeedPermission()) {
   		//	加载显示地图
           initMapView();
       }
   }
   ```

3. 添加地图加载与回调代码

   ```java
   private void initMapView() {
       mapView.init("你的建筑ID", "你的开发者KEY");
   }
   
   private BRTMapView.BRTMapViewListener mapViewListener = new BRTMapView.BRTMapViewListener() {
       @Override
       public void mapViewDidLoad(BRTMapView brtMapView, Error error) {
           if (error != null) {
               return;
           }
           //地图加载成功后，显示第一个楼层
           mapView.setFloor(mapView.getFloorList().get(0));
       }
       
       @Override
       public void onFinishLoadingFloor(BRTMapView brtMapView, BRTFloorInfo brtFloorInfo) {
   
       }
   
       @Override
       public void onClickAtPoint(BRTMapView brtMapView, BRTPoint brtPoint) {
   
       }
   
       @Override
       public void onPoiSelected(BRTMapView brtMapView, List<BRTPoi> list) {
   
       }
   };    
   ```

   

4. 运行测试

检查确认测试手机处于联网状态，并且网络能正常访问。

如果需运行室内定位相关功能：请检查手机蓝牙是否处于打开状态，安卓7.0及以上系统需要打开GPS开关。



##### **五、合规使用指南**

SDK如何处理个人信息请参见隐私政策：[隐私政策](https://files.brtbeacon.net/public/app/sdk/sdk-privacy.htm)

您集成和使用我们的SDK时需要遵从个人信息保护基本要求，详情请参见：[合规使用指南](https://files.brtbeacon.net/public/app/sdk/sdk-compliance.htm)



##### 智石科技

* [智石官网](http://www.brtbeacon.com)

##### 商务合作、地图绘制咨询[4000-999-023](tel:4000999023)