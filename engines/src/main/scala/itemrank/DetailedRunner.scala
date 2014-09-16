package io.prediction.engines.itemrank

import io.prediction.controller.EmptyParams
import io.prediction.controller.EngineParams
import io.prediction.controller.Workflow
import io.prediction.controller.WorkflowParams

import com.github.nscala_time.time.Imports._

object DetailedRunner {

  def main(args: Array[String]) {

    val dsp = EventsDataSourceParams(
      appId = 1,
      itypes = None,
      actions = Set("view", "like", "dislike", "conversion", "rate"),
      startTime = None,
      untilTime = None,
      attributeNames = AttributeNames(
        user = "pio_user",
        item = "pio_item",
        u2iActions = Set("view", "like", "dislike", "conversion", "rate"),
        itypes = "pio_itypes",
        starttime = "pio_starttime",
        endtime = "pio_endtime",
        inactive = "pio_inactive",
        rating = "pio_rate"
      )
    )

    val pp = new PreparatorParams(
      actions = Map(
        "view" -> Some(3),
        "like" -> Some(5),
        "conversion" -> Some(4),
        "rate" -> None
      ),
      conflict = "latest"
    )

    val randomAlgoParams = new RandomAlgoParams()
    val mahoutAlgoParams = new MahoutItemBasedAlgoParams(
      booleanData = true,
      itemSimilarity = "LogLikelihoodSimilarity",
      weighted = false,
      nearestN = 10,
      threshold = 5e-324,
      numSimilarItems = 50,
      numUserActions = 50,
      freshness = 0,
      freshnessTimeUnit = 86400,
      recommendationTime = Some(DateTime.now.millis)
    )

    val sp = new EmptyParams()

    val engine = ItemRankEngine()
    val engineParams = new EngineParams(
      dataSourceParams = dsp,
      preparatorParams = pp,
      algorithmParamsList = Seq(("mahout", mahoutAlgoParams)),
      // Seq(("rand", randomAlgoParams))
      // Seq(("mahout", mahoutAlgoParams))
      servingParams = sp
    )

    val mp = new DetailedMetricsParams()
    Workflow.runEngine(
      params = WorkflowParams(
        batch = "Imagine: Local ItemRank Engine",
        verbose = 3),
      engine = engine,
      engineParams = engineParams,
      metricsClassOpt = Some(classOf[ItemRankDetailedMetrics]),
      metricsParams = mp
    )
  }
}
