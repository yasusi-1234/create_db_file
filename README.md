# Insert Makerとは？
DB(データベース)のInsert文を作成するWebアプリケーションです。
## アプリケーションURL
**Excel・CSVファイルを元に作成するページ**  
[https://calm-gorge-01249.herokuapp.com/home](https://calm-gorge-01249.herokuapp.com/home)
<br><br>
**Excel・CSVファイルが無い状態から作成するページ**  
[https://calm-gorge-01249.herokuapp.com/from_zero/form
](https://calm-gorge-01249.herokuapp.com/from_zero/form)  
<br>
どなたでも無料でご利用いただけますので、気軽にご利用ください。
## 使用技術
### 言語・フレームワーク等
* Spring Boot(フレームワーク)
  * JPA ・ ThymeLeaf ・ AOP ・ SpringSecurity 
* Java
* HTML5
* Css(Sass)
* JavaScript
### データベース
* PostgreSQL
###インフラ
* Herokuサーバー

## 利用方法
Webページの方に記載しております

##作成した理由・背景
Spring Boot開発の際、data.sqlというファイルをsrc/main/resources配下に置くことでSpring起動時にデータをテーブルにインサートすることができる大変便利な機能があります。

<br>
data.sqlを作成する際少量のInsert文書く分には特に困りませんが、大量のデータを投入したい場合はInsert文を記述するのが大変な作業となってし
まいます。
<br>
<br>
また、Insert文を作成する際に記述ミスをしてしまうこともしばしばあります。
例えばValuesの項目にシングルクォーテーションを付け忘れたりする事等があげられます。
これらの課題を解決したいと思いこのアプリケーションを開発しました。

## アプリケーション機能
* Excel・CSVファイルを読み込み、そのデータ情報を元にInsert文を作成できる。
* Excel・Csvファイルが手元に無くても、利用者にカラム情報を入力フォームに入力してもらう事でInsert文を作成できる。

### Excel・CSVファイルを元にInsert文を出力する機能
<table>
  <tr>
    <th>項目</th>
    <th>詳細</th>
    <th>例外</th>
  </tr>
  <tr>
    <td>ファイルアップロード</td>
    <td>ユーザーにCSV・Excelファイルをアップロードしてもらい、そのファイルを読み込む</td>
    <td>Csv・Excel以外のファイル、ヘッダー無し・データ部分が無い場合はやり直しを施す旨のメッセージ通知をする</td>
  </tr>
  <tr>
    <td>フォーム出力</td>
    <td>読み込んだデータのヘッダーの値を、DBのカラムに紐づけて一つのObjectとして組み込む</td>
    <td>無し</td>
  </tr>
  <tr>
    <td>フォームカスタマイズ</td>
    <td>ユーザーに、ファイル名・テーブル名を選んでもらう。またカラムごとのカラム名・値(カラムの値が文字列か数値か)・カラムを含むか等の選択をしてもらう</td>
    <td>フォームのテーブル名が空の場合・全てのカラムを除外して作成ボタンを押した場合には、やり直しを施す旨のメッセージを通知する</td>
  </tr>
  <tr>
    <td>Insert文生成</td>
    <td>上記で設定したフォーム情報を元にInsert文を生成する</td>
    <td>ユーザーからアップロードされたファイルをサーバー側に保存しているが、タイムアウト処理により消されている場合は、やり直しを施す旨のメッセージを表示し、ホーム画面に遷移させる</td>
  </tr>
  <tr>
    <td>ファイルダウンロード</td>
    <td>上記で生成したInsert文を(ユーザが指定したファイル名).sqlファイルとしてダウンロードする</td>
    <td>無し</td>
  </tr>
</table>

### ユーザーファイル補完・削除機能
<table>
  <tr>
    <th>項目</th>
    <th>詳細</th>
    <th>例外</th>
  </tr>
  <tr>
    <td>ファイル補完</td>
    <td>ユーザーからアップロードされたファイルを(/tmp/(日付情報のyyyyMMddHHmmssSSS_) + ファイル名.拡張子)として、一時ディレクトリに保存する</td>
    <td>無し</td>
  </tr>
  <tr>
    <td>ファイル削除1</td>
    <td>指定されたファイルパスの保管されているファイルを削除する</td>
    <td>指定されたファイルパスが存在しない場合には、予期しないエラーが発生しました旨を出力しHome画面に遷移させる</td>
  </tr>
  <tr>
    <td>ファイル削除2</td>
    <td>10分おきに(/tmp)無いのディレクトリを走査し、10分以上経過しているファイルを削除する</td>
    <td>何れかの要因でファイル削除に失敗した場合はLogに出力する</td>
  </tr>
</table>

### Excel・CSVファイル無しにInsert文を出力する機能
<table>
  <tr>
    <th>項目</th>
    <th>詳細</th>
    <th>例外</th>
  </tr>
  <tr>
    <td>入力フォーム追加</td>
    <td>ユーザーに必要なカラム情報【姓(苗字)・名(名前)・メールアドレス・数値・文字・日付と時刻・日付・時刻】と必要数を加えるフォームから追加してもらい、ユーザー独自のフォームを作成する</td>
    <td>無し</td>
  </tr>
  <tr>
    <td>フォームカスタマイズ1</td>
    <td>ユーザーに、作成する要素数(データベースのレコード数)・ファイル名・テーブル名を選んでもらう。</td>
    <td>無し</td>
  </tr>
  <tr>
    <td>フォームカスタマイズ2</td>
    <td>ユーザーが追加した独自のカラム情報ごとに、カラム名や出力方式を設定してもらう</td>
    <td>フォームのテーブル名が空の場合・ユーザー独自のフォームの値などに異常がある場合は、やり直しを施す旨のメッセージを通知する</td>
  </tr>
  <tr>
    <td>独自フォームの個別削除</td>
    <td>ユーザーが追加した独自のカラム情報を個別単位で削除する</td>
    <td>無し</td>
  </tr>
  <tr>
    <td>Insert文生成(Insert文)</td>
    <td>上記で設定したフォーム情報を元にInsert文を生成する</td>
    <td>無し</td>
  </tr>
  <tr>
    <td>ファイルダウンロード(Insert文)</td>
    <td>上記で生成されたInsert文を(ユーザーが指定したファイル名).sqlダウンロードする</td>
    <td>無し</td>
  </tr>
  <tr>
    <td>Excelファイル生成</td>
    <td>上記で設定したフォーム情報を元にExcelファイルを生成する</td>
    <td>無し</td>
  </tr>
  <tr>
    <td>ファイルダウンロード(Excel)</td>
    <td>上記で生成されたExcelファイルを(ユーザーが指定したファイル名).xlsxをダウンロードする</td>
    <td>無し</td>
  </tr>
</table>

## その他
* 今後の改善点など

```puml
actor ユーザー

ユーザー -> コントローラー : ホーム画面アクセス
activate コントローラー
コントローラー --> ユーザー : ホーム画面View
deactivate コントローラー

ユーザー -> コントローラー : 添付ファイル送信
activate コントローラー
alt 添付ファイル正常な場合
    コントローラー -> サービス: 添付ファイル保存
    note top
      日付日時情報:
      yyyyMMddHHmmssSSS
      + ファイル名で保存
    end note
    activate サービス
    サービス --> コントローラー: 保存Path返却
    deactivate サービス
    
    コントローラー -> セッション: 保存Pathを追加
    activate セッション
    deactivate セッション
    コントローラー --> ユーザー: カスタム画面View
else 添付ファイルが不正な場合
    コントローラー --> ユーザー: ホーム画面View(エラーメッセージ)
    note right
      CSV・Excel形式以外のファイル(サポート外)
      ヘッダー・データ部分が無いファイル等の場合
    end note
deactivate コントローラー
end

ユーザー -> コントローラー : フォーム入力＆ダウンロードリクエスト
alt 入力フォームが正常な場合
    activate コントローラー
    コントローラー -> セッション: pathの取得リクエスト
    activate セッション
    セッション --> コントローラー: ファイルpath返却
    deactivate セッション
    コントローラー -> サービス: path&フォーム情報送信
    activate サービス
    サービス --> コントローラー: Insert文生成内容返却
    deactivate サービス
    コントローラー --> ユーザー: sqlファイルダウンロード
else 入力フォームが異常な場合
    コントローラー --> ユーザー: カスタム画面(エラーメッセージ)
    deactivate コントローラー
end

ユーザー -> コントローラー : 処理終了
activate コントローラー
コントローラー -> セッション: ファイルPath情報削除
activate セッション
セッション --> コントローラー: ファイルPath返却
deactivate セッション
コントローラー -> コントローラー: 添付保存ファイル削除
コントローラー --> ユーザー : ホーム画面View(利用サンクスメッセージ)
deactivate コントローラー

```

```puml
actor ユーザー

ユーザー -> コントローラー:0から作成ページアクセス
activate コントローラー
コントローラー --> ユーザー:0から作成View返却
deactivate コントローラー

ユーザー -> コントローラー:フォーム追加リクエスト
alt リクエスト値正常
    activate コントローラー
    コントローラー -> フォーム: 個別フォーム情報を加える
    activate フォーム
    deactivate フォーム
    コントローラー --> ユーザー: 0から作成View返却
else リクエスト値異常
    コントローラー --> ユーザー: 0から作成View返却(エラーメッセージ)
    deactivate コントローラー
end

ユーザー -> コントローラー: ファイル出力リクエスト
alt リクエスト値異常
    activate コントローラー
    note right
        Excelファイル
        sqlファイル
        のいずれか
    end note
    コントローラー --> ユーザー: 0から作成View返却(エラーメッセージ)
else リクエスト値正常
    alt sqlファイル
        コントローラー -> サービス: Insert文生成リクエスト
        activate サービス
        opt 個別フォームにfirstNameかlastNameが存在する
            サービス -> データベース
            activate データベース
            データベース --> サービス: 名前情報返却
            deactivate データベース
        end
        サービス -> サービス: Insert文生成
        サービス -> コントローラー: Insert文返却
        deactivate サービス
        コントローラー --> ユーザー: Insert文ダウンロード
    else excelファイル
        コントローラー -> サービス: Excel生成リクエスト
        activate サービス
        opt 個別フォームにfirstNameかlastNameが存在する
            サービス -> データベース
            activate データベース
            データベース --> サービス: 名前情報返却
            deactivate データベース
        end
        サービス -> サービス: Excel用データ生成
        サービス -> コントローラー: Excel用データ返却
        deactivate サービス
        コントローラー --> ユーザー: Excelファイルダウンロード
        deactivate コントローラー
    end
end

```

```puml
abstract class AbstractList
abstract AbstractCollection
interface List
interface Collection

List <|-- AbstractList
Collection <|-- AbstractCollection

Collection <|- List
AbstractCollection <|- AbstractList
AbstractList <|-- ArrayList

class ArrayList {
Object[] elementData
size()
}

enum TimeUnit {
DAYS
HOURS
MINUTES
}
```

