# KafaAds Library Admob And FAN for Android

## Table of Contents
+ [About](#about)
+ [Getting Started](#getting_started)
+ [Usage](#usage)
+ [Contributing](../CONTRIBUTING.md)

## About
This library help to display ads from Admob or FAN, you can use it for simple display Ads to your app

## Getting Started
This library support Admob and FAN provider
- Admob
  - Admob SDK 19.7.0
  - Google Play Services 19.0.0
  - Lifecycle-runtime 2.2.0
  - Lifecycle-compiler 2.2.0
  - Lifecycle-Extensions 2.2.0
- FAN
  - FAN SDK 5.10.1
- Retrofit 2.7.1
- Gson 2.9.0
- Glide 4.13.1

### Prerequisites
What things you need to install the software and how to install them, use Jitpack

```Groove
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### Installing

A step by step series of examples that tell you how to get a development env running.

Add the dependency

```Groove
dependencies {
	implementation 'com.github.spylight-zz:KafaAdsLibrary:1.0.1'
}
```

That's it!

## Usage
To use the library for display ads are simple, let's code.
##### Declare class
```java
private KafaAds ads;
```
The library comes with the model, so you can store your data into the model and pass it into your app
```java
KafaModel kafaModel = new KafaModel();
kafaModel.set_appid(<Your-AppId> or from json object ex:jo.optString(KafaConst.APPID));
kafaModel.set_banner(<Your-BannerId> or from json object ex:jo.optString(KafaConst.BANNER));
...<etc>
KafaAds.setAds(kafaModel);
```
Now you just call the model and pass it into your function or class
```java
KafaAds.getAds().get_appid()
KafaAds.getAds().get_banner()
..<etc>
```
##### Initialize Ads
To initialize ads, you can call the function like
```java
KafaAds.initAd(context,<Your-AppId> or from model ex: KafaAds.getAds().get_appid());
```
##### Banner
This example to display BannerAd
```java
/* Banner
 * AdType - BANNER / HALF_BANNER
 */
//for Admob 
ads = new KafaAds.Builder(context)
    .setContainer(findViewById(R.id.lyt_banner))
    .setAd(new Ad(AdName.ADMOB, AdType.BANNER, KafaAds.getAds().get_banner()))
    .build();
ads.load();

//for FAN
ads = new KafaAds.Builder(context)
    .setContainer(findViewById(R.id.lyt_banner))
    .setAd(new Ad(AdName.FACEBOOK, AdType.BANNER, "YOUR-FAN-BANNERID"))
    .build();
ads.load();

//for Admob and FAN
ads = new KafaAds.Builder(context)
    .setContainer(findViewById(R.id.lyt_banner))
    .setAd(new Ad(AdName.FACEBOOK, AdType.BANNER, "YOUR-FAN-BANNERID"))
    .setAd(new Ad(AdName.ADMOB, AdType.BANNER, KafaAds.getAds().get_banner()))
    .build();
ads.load();
```



## Authors
Made with ❤ by [@spylight-zz](https://www.github.com/spylight-zz)

[Buy me a coffee](https://www.buymeacoffee.com/spylight)















