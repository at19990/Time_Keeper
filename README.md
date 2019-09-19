
# タイムキーパー (Androidアプリ)  

<img src="https://user-images.githubusercontent.com/16556629/64978540-ba3fba00-d8f0-11e9-881f-3c6d644a58e5.png" width="100px">

<img src="https://user-images.githubusercontent.com/16556629/64977173-dc840880-d8ed-11e9-8626-7b3e2a9d0ac3.JPG" width="320px">

プレゼンや学会の口頭発表の練習・タイムキープに使えるシンプルなアプリを目指しました。  

全体の制限時間、1鈴・2鈴のタイミングを設定してお使いください。 (初期状態は 全体:10分・1鈴:5分前・2鈴:3分前になっています)  

2鈴以降は、残り時間の表示が赤字になります。  

時間を再設定する際には一時停止してから行ってください。  

また、リセットボタンを押すと直前の設定に戻すことができます。  

現在設定可能な最小単位は1分です。(30秒単位など細かい設定はできません)  

不正な入力値に関しては一応エラーを返すようになっている...はず。  

バックグラウンドでも一応動作します。  

## インストール方法  

いまのところPlayストア未登録なので、`git clone` してandroid studio からビルドするなどして使用してください。  
  
  
## 環境  

システム的には、Android 5.0 以降をサポートしています。  

Android 6.0 の実機で動作を確認済。  
  
## 参考  

[【はじめてのAndroidアプリ開発】タイマーアプリを作ってみよう](https://blog.codecamp.jp/android-app-development-1)
