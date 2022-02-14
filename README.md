[![build-all](https://github.com/extact-io/rms/actions/workflows/build-all.yml/badge.svg)](https://github.com/extact-io/rms/actions/workflows/build-all.yml)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=extact-io_rms&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=extact-io_rms)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=extact-io_rms&metric=ncloc)](https://sonarcloud.io/dashboard?id=extact-io_rms)
[![BCH compliance](https://bettercodehub.com/edge/badge/extact-io/rms?branch=main)](https://bettercodehub.com/)
# Rental Management System Application
Rental Management SystemはOracle Helidonを用いてMicroProfileの利用法や効果を確認することを目的としたリファレンス的なアプリケーションです

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [アプリケーションの説明](#%E3%82%A2%E3%83%97%E3%83%AA%E3%82%B1%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3%E3%81%AE%E8%AA%AC%E6%98%8E)
- [ビルドと動作方法](#%E3%83%93%E3%83%AB%E3%83%89%E3%81%A8%E5%8B%95%E4%BD%9C%E6%96%B9%E6%B3%95)
- [アプリケーションアーキテクチャ](#%E3%82%A2%E3%83%97%E3%83%AA%E3%82%B1%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3%E3%82%A2%E3%83%BC%E3%82%AD%E3%83%86%E3%82%AF%E3%83%81%E3%83%A3)
- [利用ツール](#%E5%88%A9%E7%94%A8%E3%83%84%E3%83%BC%E3%83%AB)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# アプリケーションの説明
## アプリケーション機能
会員がレンタル品を予約するアプリケーションで管理機能といてマスターデータなどをメンテナンスする機能を持っています。なお、実装している機能は予約するまでで実際にレンタルを行う機能はまだ持っていません
| 分類 | 機能 | 内容 |
|------|------|------|
|会員機能|レンタル品検索|レンタル品を検索し予約状況の確認や予約も行うことができます|
||レンタル品予約|予約したいレンタル品を選択しレンタル期間を指定した予約ができます|
||予約確認|自分の予約を確認したりキャンセルしたりすることができます|
|管理機能|レンタル品管理|レンタル品の登録や更新などを行うことができます|
||予約管理|登録されたレンタル予約の削除や変更を行うことができます|
||ユーザ管理|ユーザの登録や更新、削除などを行うことができます|

このリポジトリから提供されるアプリのUIはコンソールによるCUIとなります。ReactによるSPAなUIも別途用意していますので、興味があるかたは[こちら](https://github.com/extact-io/rms-ui-react)へどうぞ

# ビルドと動作方法
## 手っ取り早くアプリで動かす
All In Oneで[Local&JPA](#local接続時の物理配置)で起動するアプリケーションを用意しています。jpackageでOSごとの実行形式ファイルに変換したものでzipファイルを解凍するだけで利用可能です
 - Windows版は[こちらから](https://github.com/extact-io/rms/releases/download/v1.0.0-beta.1/RmsConsoleWin.zip)
 - Mac版は[こちらから](https://github.com/extact-io/rms/releases/download/v1.0.0-beta.1/RmsConsoleMac.zip)

解凍後、実行形式ファイル(.exeまたは.app)をダブルクリックするとSwingのコンソールアプリが起動します。起動しますのでデフォルトで用意している[こちら](#デフォルトで用意しているidpassword)のID/passwordを使ってログインしてください

## 自分でビルドして動かす
cloneもしくはzipで取得したrepositoryのコードをビルドして実行します。JDK SE 11以上を前提としビルドにはMavenを利用します。上記のAll In Oneアプリとは異なりここでは[Remote&JPA](#remote接続時の物理配置)で動作させます。

1. dependencyのローカルインストール  
``` shell
# Clone this repository
git clone https://github.com/extact-io/rms.git
# Go into the repository
cd rms
# Install dependencies
mvn -Pcli,all clean install -DskipTests=true
```

2. サーバ側（RESTリソースアプリ）のビルドと起動
``` shell
# Go into the app directory
cd rms-server
# Build the app
mvn -Pcli,copy-libs clean package -DskipTests=true
# Run the app
java -Drms.h2.script=classpath:init-rms-demo.ddl -jar target/rms-server.jar
```

3. クライアント側（コンソールアプリ）のビルドと起動 *サーバを起動しているコンソールとは別のコンソールで行う
``` shell
# Go into the app directory
cd /path/to/your/rms_dir
cd rms-client-ui-console
# Build the app
mvn -Pcli,copy-libs clean package -DskipTests=true
# Run the app
java -jar target/rms-client-ui-console.jar
```

クライアントアプリを起動するとログインを求められますのでデフォルトで用意しているID/passwordを入力してください

## デフォルトで用意しているID/password
|種別|ID|password|
|----|--|--------|
|会員|edamame|edamame|
|管理者|admin|admin|

# アプリケーションアーキテクチャ
## 論理アーキテクチャ
論理アーキテクチャはCleanArchitectureでもヘキサゴナルでもなんでもありません  
Domainレイヤをリラックスレイヤにした一般的なレイヤーアーキテクチャになります

![レイヤ](/docs/parts/logical_arch.png)

- UIレイヤ・・言わずもがなUI
- APIレイヤ・・レンタル予約システムに対するClient側の公開JavaAPI
- Adoptorレイヤ・・Client側の公開JavaAPIに対する実装。接続方式の違いを吸収する
- WebAPIレイヤ・・レンタル予約システムアプリをRESTで外部に公開する
- Serivceレイヤ・・レンタル予約システムのアプリケーション実装
- Domainレイヤ・・エンティティや制約
- Persistenceレイヤ・・データの永続化
- Platformレイヤ・・業務依らない基盤的な仕組み

なお、レイヤ間やパッケージや他ライブラリへの依存関係のルールはArchUnitの実装で定義しています。詳細に興味があるかたは[そちら](/rms-client-ui-console/src/test/java/io/extact/rms/client/console/LayerDependencyArchUnitTest.java)もどうぞ

:pushpin: POINT
レンタル予約システムのアプリケーションの実体であるServiceレイヤへの接続は利用するAPIレイヤの実装を切り替えるだけでRemoto/Localのどちらでも動作するようになっています。また、Persistenceレイヤも設定を切り替えるだけでFileによる永続化とJPAを使ったRBDへの永続化のどちらでも使えるようにしています

:speech_balloon:（ボソボソ）
CleanArchitectureやヘキサゴナルとかよく聞きますがその最たるモチベーションは外部と接するモジュールをプラガブルに切り替えられるようにすることと考えています。今回のレンタル予約システムはレイヤーアーキテクチャを採っていますがUIと永続化のプラガブルな切り替えは実現できています。ですので、CleanArchitectureやヘキサゴナルと下層のDomainレイヤなどをリラックスレイヤと定義したレイヤーアーキテクチャと本質的な違いやメリットを理解できません。これらはモノの見方や考え方の違いだけで実質的な違いはないと思うので、個人的にはレイヤーアーキテクチャで考えた方が直観的で管理しやすいのではないかとは感じています

## 物理構造
application.jarはserviceレイヤ、persistenceレイヤ、Domainレイヤのモジュールを格納し、server.jarにはWebAPIレイヤのモジュールを格納しています

![全体物理配置](/docs/parts/runtime_overview.drawio.svg)

## 配置構造
レンタル予約システムはLocal接続とRemote接続の2つをサポートし、接続形態ごとに配置に必要となるモジュールは異なります

### Local接続時の物理配置
シングルプロセスで動作しapi-local.jarのアダプタ実装からレンタル予約システムアプリの実体であるapplication.jarへ直接依存依存させています

![Local接続時の物理配置](/docs/parts/runtime_lolcal.drawio.svg)

### Remote接続時の物理配置
Client/Sever方式で動作しClientからSeverモジュールへの直接的な依存はなくserver.jarによるWebAPIを経由しapplication.jarの機能提供を受けます

![Remote接続時の物理配置](/docs/parts/runtime_remote.drawio.svg)


## 利用ライブラリと準拠API
- ランタイム系
  - Java11（OpenJDK v11.0.8）
  - Bean Validation 2.0(Hibernate Validator 6.1.2)
  - [MicroProfile 3.3](https://download.eclipse.org/microprofile/microprofile-3.3/microprofile-spec-3.3.html)
    - Server（Netty 4.1）
    - CDI 2.0（Weld 3.1）
    - JAX-RS 2.1（Jersey 2.32）
    - [MicroProfile Rest Client 1.4](https://download.eclipse.org/microprofile/microprofile-rest-client-1.4.0/microprofile-rest-client-1.4.0.html)
    - [MicroProfile Config 1.4](https://download.eclipse.org/microprofile/microprofile-config-1.4/microprofile-config-spec.html)
    - [MicroProfile JWT-AUTH 1.1.1](https://download.eclipse.org/microprofile/microprofile-jwt-auth-1.1.1/microprofile-jwt-auth-spec.html)
    - [MicroProfile Health 2.2](https://download.eclipse.org/microprofile/microprofile-health-2.2/microprofile-health-spec.html)
    - [MicroProfile OpenAPI 1.2](https://download.eclipse.org/microprofile/microprofile-open-api-1.2/microprofile-openapi-spec-1.2.html)
  - Helidon MP v2.4.2
    - [ReactiveWebserver](https://helidon.io/docs/v2/#/se/webserver/01_introduction)
    - [Helidon MP JPA](https://helidon.io/docs/v2/#/mp/jpa/01_introduction) (EclipseLink 2.7.5)
    - [CDI extension for HikariCP](https://helidon.io/docs/v2/#/mp/extensions/02_cdi_datasource-hikaricp) (HikariCP 3.4)
    - [CDI extension for JTA](https://helidon.io/docs/v2/#/mp/extensions/05_cdi_jta) (Weld 3.1)
    - [CORS in Helidon MP](https://helidon.io/docs/v2/#/mp/cors/01_introduction)
    - [Configuration Secrets in Helidon Config](https://helidon.io/docs/v2/#/mp/security/03_configuration-secrets)
  - JWT
    - [jose4j 0.7.9](https://bitbucket.org/b_c/jose4j/wiki/Home)
  - H2 Database
  - [Text-IO 3.4.1](https://github.com/beryx/text-io) -> コンソールアプリ向けのframework
- テスト系
  - JUnit 5.7
  - [ArchUnit 0.22](https://www.archunit.org/)
  - [Helidon MP Testing with JUnit5](https://helidon.io/docs/v2/#/mp/testing/01_testing)

*カッコ内は利用している実装

## 主な仕組みと実現方式
|仕組み|方式|
|------|----|
|認証|独自認証＋JTW Bearer Token(MicroProfile JTW Authentication)|
|認可|MicroProfile JTW RBAC＋JakartaEE Security API(JSR 375)|
|API定義|MiciroProfile OpenAPIによるOAS出力|


## platformモジュールの提供機能
業務依らない基盤的な仕組みとしてplatformモジュールが提供する機能  
(platformモジュールのコメントを1部英語で記載しているためOSSのコードをパクったように見える箇所がありますが全てスクラッチから作成しています:sweat_smile:)

| パッケージ | 主要クラス | 機能 | 実装API |
|------------|-------------|------|---------|
| config | [ExternalPathConfigProvider](/rms-platform/src/main/java/io/extact/rms/platform/config/ExternalPathConfigProvider.java) | システムプロパティで指定された設定ファイルの読み込み | MicroProfile Config |
|  | [ResourcePathUnderConfigProvider](/rms-platform/src/main/java/io/extact/rms/platform/config/ResourcePathUnderConfigProvider.java) | 特定のリソースパス配下に配置されている設定ファイルの自動読み込み | MicroProfile Config |
| evn | [Environment](/rms-platform/src/main/java/io/extact/rms/platform/env/Environment.java) | `MANIFEST.MF`などから取得した環境情報の提供 | － |
| extension | [ApplicationInitializerCdiExtension](/rms-platform/src/main/java/io/extact/rms/platform/extension/ApplicationInitializerCdiExtension.java) | CDIExtensions<br/>・設定ファイルで指定されたクラスをCDIBeanとして登録する<br/>・アノテーションと設定ファイルの情報からCDIBeanの有効/無効化を行う | JakartaEE CDI |
| health | [MemoryHealthCheck](/rms-platform/src/main/java/io/extact/rms/platform/health/MemoryHealthCheck.java) | メモリ状況をもとにlivenessとreadnessの応答を返す |
| jwt | [JsonWebTokenGenerator](/rms-platform/src/main/java/io/extact/rms/platform/jwt/provider/JsonWebTokenGenerator.java) | 認証情報をJsonWebTonkenにする | MicroProfile JWT |
|  | [JsonWebTokenValidator](/rms-platform/src/main/java/io/extact/rms/platform/jwt/consumer/JsonWebTokenValidator.java) | 受け取ったJsonWebTokenを検証する | MicroProfile JWT |
| log | [MpConfigDump](/rms-platform/src/main/java/io/extact/rms/platform/debug/MpConfigDump.java) | 優先度と上書き設定が解決済みの設定情報を一覧でログ出力する | － |
  |  | [ServerHeaderDumpFilter](/rms-platform/src/main/java/io/extact/rms/platform/debug/ServerHeaderDumpFilter.java) | RESTリソースに対するリクエストのヘッダ情報をログ出力する |
|  | [SessionLogBridge](/rms-platform/src/main/java/io/extact/rms/platform/debug/ext/SessionLogBridge.java) | EclipseLinkログのSLF4Jブリッジ | (EclipseLink) |
| provider | [JsonbRmsConfig](/rms-platform/src/main/java/io/extact/rms/platform/jaxrs/converter/JsonbRmsConfig.java) | JSON変換に適用するアプリ独自のコンバータ | JakartaEE JSONB |
|  | [ParamRmsConverterProvider](/rms-platform/src/main/java/io/extact/rms/platform/jaxrs/converter/ParamRmsConverterProvider.java) | リクエストパラメータに適用するアプリ独自のコンバータ | JakartaEE JAX-RS |
|  | [PageNotFoundExceptionMapper](rms-platform/src/main/java/io/extact/rms/platform/jaxrs/mapper/PageNotFoundExceptionMapper.java) | ステータスコード404に対する例外ハンドラ | JakartaEE JAX-RS |
|  | [UnhandledExceptionMapper](rms-platform/src/main/java/io/extact/rms/platform/jaxrs/mapper/UnhandledExceptionMapper.java) | 未捕捉例外をハンドルしてログ出力する例外ハンドラ | JakartaEE JAX-RS |
| role | [RoleSecurityDynamicFeature](/rms-platform/src/main/java/io/extact/rms/platform/role/RoleSecurityDynamicFeature.java) | RESTリソースに対するRoleベースの認可制御 | JakartaEE Security API |
| util | [ResourceUtils](/rms-platform/src/main/java/io/extact/rms/platform/util/ResourceUtils.java) | クラスパスリソースの検索ユーティリティ | － |
| validate | [ValidateParamInterceptor](/rms-platform/src/main/java/io/extact/rms/platform/validate/ValidateParamInterceptor.java) | バリデーショングループも指定可能なメソッドバリデーションの仕組み | JakartaEE Bean Validation |

# 利用ツール
| プロセス | 利用ツール |
|----------|----------|
|build|Maven|
|CI|[GitHub Actions](/.github/workflows/build-all.yml)|
|CD|[GitHub Actions](/.github/workflows/deploy-aws.yml) + [AWS CodeDeploy](/rms-server/env/deployment/appspec.yml)|
|Static analysis|[SonarClooud](https://sonarcloud.io/summary/overall?id=extact-io_rms), [Mave Site Generator](https://extact-io.github.io/web-site/rms/site/modules/)|
|API Doc|[Redoc](http://app.extact.io/static/)|

