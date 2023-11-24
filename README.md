# SysB_wtm

天気予報行動提案Androidアプリ「WTM（Where to me）」 / 
project for a class named System development B TCU

### Members
* Manato Yamashita（リーダ, 実装, デザイン）
* Yuna Kishi（実装, デザイン）
* Mana Ochiai（文書）
* Haruna Iwasa（文書）

<img width="203" alt="view" src="https://github.com/ManatoYamashita/SysB_wtm/assets/95745485/ffc3c907-aab4-4bd6-8922-60256c69eda9">

## Functions
* 天気表示機能
  「天気」ボタンを押すと、出発地, 経由地, 目的地を入力すると3箇所の天気を表示
* 経路探索機能
  「マップ」ボタンを押すと、出発地から目的地までの経路をGoogle mapで表示
* おすすめ見学ポイント機能
  「スポット」ボタンを押すと、Googleで目的地のおすすめスポットを表示する
* 待ち合わせ機能
  「待ち合わせ」ボタンを押すと、Googleで目的地周辺の駅を検索
* メール機能
  「メール」ボタンを押すと、宛先、件名、内容を入力してGmailで送信

### Sub Function
* クソ広告
  画面下部にいかがわしい広告を表示。タップすると(東京都市大学 メディア情報学部 情報システム学科のページ)[https://informatics.tcu.ac.jp/faculty/system/] にリダイレクトする
  
## Directorys

  * src/main
    * MainActivity.java（または相当するファイル）
      現在地、経由地、目的地をキーボードから受け取る。それらの情報をもとに、天気、経路、観光地、待ち合わせ場所を確認できるボタンをそれぞれ用意されている。天気を表示するボタンを押下するごとに、現在地、経由地、目的地の天候情報を表示。画面右上のアクションバーからメールを送信できる。マップを表示するボタンを押すとGoogle マップから経路が表示され、おすすめスポットを表示するボタンを押すとGoogle検索で「目的地 + おすすめスポット」で検索。待ち合わせボタンを押すとGoogleマップから「出発地 + 近くの駅」で検索する。
    * AddActivity.java
    * MailActivity.java
      送信先メールアドレス、要件、内容を入力できるテキストボックスからメールを作成し、送信ボタンからメールを送信可能。
    * SpotShow.java
      MainActivity.javaから受け取った目的地から近くのおすすめのスポットをGoogle検索する。
  * res／drawable-hdpiフォルダ内
    * launcher用アイコン画像ファイル
      画像を貯蓄し名称を定める。その名称を各所から呼び出された際に画像の情報を送る。
    * その他の画像ファイル
  * res／layoutフォルダ内
    * activity_main.xml　（部品レイアウト記述ファイル）
      MainActivity.javaで入力した内容に準じて画面のレイアウトを行う。リストやテキスト、ボタンの配置や大きさを設定し表示させる。
    * activity_mail.xml
      MailActivity.javaで入力した内容に準じて画面のレイアウトを行う。リストやテキスト、ボタンの配置や大きさを設定し表示させる。
  * res／valuesフォルダ内
    * dimensions.xml
    * strings.xml
      MainActyvity.javaで利用するテキストの情報を格納する。各所で呼び出された際に対応する文字列情報を送る。
    * styles.xml
    * colors.xml
    * menu.xml
      アクションバーを使用するための設定が記述されている。 
  * AndroidManifest.xml
    システムを動かす上での基礎設定を記述している。 MainActivity.java上必要な権​限の設定など、逐次記入する。
  　・その他（依存関係）

## Fllow 
* MainActivity
  <img width="219" alt="スクリーンショット 2023-11-24 15 04 21" src="https://github.com/ManatoYamashita/SysB_wtm/assets/95745485/d67a153a-e24f-4766-82b7-abef02acdfd2">

* MailActivity
  <img width="273" alt="スクリーンショット 2023-11-24 15 05 01" src="https://github.com/ManatoYamashita/SysB_wtm/assets/95745485/950c42fd-d21b-4464-af61-25cb30d6d214">
