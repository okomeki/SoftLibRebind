# SoftLibRebind
Java, JSON, CBOR, YAML, etc bind tool

## 概要

JavaとJSONとその他いろいろの形式を変換するための枠組みです Jakarta JSON Binding などでは各形式に対応する
ファイル形式とオブジェクト形式の2つあると思うので、それぞれ個別のフォーマットとして扱えます。
改行が違うとか派生形式も可能かもしれません。

Javaの該当型への変換は classを指定するだけで簡単です。

  JsonValue json = Rebind.valueOf( object, JsonValue.class );

型に対応していれば、object を JsonValue型やList, Map, YAML や CBOR といった形で出力できるようになります。

  型class の他に 出力型を指定する TypeFormat という interface にあわせて自由に出力形式を作ることができます。
  たとえば JSON の改行ありなしなど詳細の設定もできますね。

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
```
<dependency>
  <groupId>net.siisise<groupId>
  <artifactId>softlib-rebind</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <type>jar</type>
</dependency>
```
まだSNAPSHOTです。

## 予定

   IO系、SQL拡張、ASN1対応など?

## LICENSE

 Apache 2.0
 okomeki または しいしせねっと

