# SoftLibRebind
Java, JSON, CBOR, YAML, etc bind tool

## 概要

JavaとJSONとその他いろいろの形式を変換するための枠組みです Jakarta JSON Binding などでは各形式に対応する
ファイル形式とオブジェクト形式の2つあると思うので、それぞれ個別のフォーマットとして扱えます。
改行が違うとか派生形式も可能かもしれません。

現状各SoftLibで対応していそうなもの
 - Java Object
   - Object, List, Map, 基本型など
 - JSON
   - text
   - JSONP Object (Java EE, Jakarta EE, SoftLibJSON)
 - ASN.1
   - X.690 DER
   - X.509 方面
 - CBOR
 - YAML

対応予定のもの
 - YAML, CBOR全部
 - X.690系の他のもの
 - SQL (JDBC) Mapping
など

Javaの該当型への変換は classを指定するだけで簡単です。

  JsonValue json = Rebind.valueOf( object, JsonValue.class );

JSON, ASN.1 DER, CBORなどの出力型は、TypeFomrat というinterface で作ることができます。

型に対応していれば、object を JsonValue型やList, Map, YAML や CBOR といった形で出力できるようになります。JSONの改行あり、なしなどを分けることもできます。

JsonValueなどデータ形式の専用型を使わず Javaの標準Class, List, Mapなどでデータを組み立てて、そのままJSON, ASN.1, CBORなど自由な型で出力が可能です。ASN.1のOBJECTIDENTIFIERなど一部は専用型が必要ですがURIなどに変換する案もあり。

内部形式 Java Collection, Java Object (PoJo? Bean?), JSON Object (Java JSON-P, Jakarta JSON-P, 独自形式)
出力形式 JSON,YAML,CBOR など

内部形式から自由な出力形式を組み合わせできます

内部形式、出力形式をJavaのサービスプロバイダ (META-INF/services) を使って拡張可能です。
YAML, CBORなどが可能です。ASN.1なども扱えるかもしれません。

## 経緯

何かできてしまったのでSoftLibJSONから独立させた

JSONからJavaに変換するのが面倒だ、ということでABNFから実装。
自動変換の形でJava reflectionでJSON Binding的なものも作ってみた。
JSON と Javaの相互変換を作っていると、JSONのバイナリ、JSONテキスト、Javaオブジェクトと3つくらいの形式を扱うことになります。
JSONは標準化もされていて独自実装もしていて、さらにJakarta系もあるので3つぐらいObject形式が加わる。
Jakarta EE系はJDK 8で扱えないので分けておきたい。
JSONのオブジェクトと書式も分離したい。
書式指定用機能を拡張したらParserっぽい形で分離成功。
他のたとえばCBORやYAMLといった形式もちょっと出力したいな、さてどうなるか。

特定の型の読み取りと出力を独立させてやれば相互変換が可能なのでは?

ということでSoftLibJSONでOMAP という形で作っていたJSON用の機能を他の形にも拡張してしまえ、となった。

読む、書くを分割する。中継形式はJavaのCollectionっぽいのでええやん、ということで組み合わせ方法を考えた。

あとはParserも組み込めれば各形式の3形態を分離できるかも。

## 構成

 SoftLibRebind Java List/Map/Object が扱える
   SoftLibJSON        JDK  8   用 JSON,  Java EE    JSON-P JSON-B 互換, 独自実装、JSON共通部分含む
   SoftLibJakartaJSON JDK 11以降用 JSON,  Jakarta EE JSON Processing, Jakarta EE JSON Binding 互換
   SoftLibYAML        YAML, CBOR など 仮分類出力用

## Maven

pom.xml のdependency は次のような感じで追加します。
SoftLibJSONなどから依存関係になっているので使いたい機能を個別に含めればSoftLibRebindも含まれます。

Java Module System JDK11以降用
```
<dependency>
  <groupId>net.siisise<groupId>
  <artifactId>softlib-rebind.module</artifactId>
  <version>0.0.5</version>
  <type>jar</type>
</dependency>
```
JDK 1.8用
```
<dependency>
  <groupId>net.siisise<groupId>
  <artifactId>softlib-rebind</artifactId>
  <version>0.0.5</version>
  <type>jar</type>
</dependency>
```
CharSequence を分離してみた 0.0.5版。
次版 0.0.6-SNAPSHOT ぐらい。

## 予定

   IO系、SQL拡張、ASN1対応など?

## LICENSE

 Apache 2.0
 okomeki または しいしせねっと

