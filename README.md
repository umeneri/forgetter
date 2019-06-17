[Slack Botの種類と大まかな作り方 - Qiita](https://qiita.com/namutaka/items/233a83100c94af033575)


# Development with Intellij
Set the Environment variables:

Edit Configuration: 
```
OAUTH2TOKEN={token}
```

# log
# circe
```
Error:(24, 22) could not find implicit value for parameter um: akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller[com.example.ChallengeEvent]
            entity(as[ChallengeEvent]) { event =>
Error:(42, 46) could not find implicit value for evidence parameter of type io.circe.Decoder[com.example.CallbackEvent]
            val event = decode[CallbackEvent](slackEventEntity).right.get

```

```
            Error:(30, 76) could not find Lazy implicit value of type io.circe.generic.decoding.DerivedDecoder[com.example.CallbackEvent]
              implicit val CallbackEventDecoder: Decoder[CallbackEvent] = deriveDecoder[CallbackEvent]
```
→ 
[could not find Lazy implicit value of type io.circe.generic.decoding.DerivedDecoder[A] implicit val comDecoder: Decoder[SolrDoc] = deriveDecoder · Issue #541 · circe/circe](https://github.com/circe/circe/issues/541#issuecomment-310952006)


           
