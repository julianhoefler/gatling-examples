/*
 * Copyright 2016-2020 Ivan Krizsan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.ivankrizsan.gatling.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.language.postfixOps

/**
 * Example Gatling load test that sends one HTTP GET requests to a URL.
 * Note that the request is redirected and this causes the request count to become two.
 * Run this simulation with:
 * mvn -Dgatling.simulation.name=HttpSimulation1 gatling:test
 *
 * @author Ivan Krizsan
 */
class HttpSimulation1 extends Simulation {

  val rampUpUsers: Int = 100
  val rampUpDuration: Int = 1

  val constantUsers: Int = 150
  val constantSeconds: Int = 90

  // RequestCount = ((constantUsers * seconds) + rampUsers) * 2

  before {
    println("***** Simulation is about to begin! *****")
  }
  after {
    println("***** Simulation has ended! ******")
  }
  val theHttpProtocolBuilder: HttpProtocolBuilder = http
    .baseUrl("http://127.0.0.1:8080")

  val theScenarioBuilder: ScenarioBuilder = scenario("Reiseloesung-Anfrage Simulation")
    .exec(
      http("Hinfahrt Direkt")
        .get("/reiseloesung?abfahrtLocation=frankfurt&ankunftLocation=erfurt" +
          "&hinfahrtDate=2022-08-01T08:00&trainType=ICE")
    )
    .exec(
      http("Hin/Rueck mit Umstieg")
        .get("/reiseloesung?abfahrtLocation=frankfurt&ankunftLocation=rostock&" +
          "hinfahrtDate=2022-08-01&rueckfahrtDate=2022-08-04T09:00&trainType=ice")
    )

  setUp(
    theScenarioBuilder.inject(
      rampUsers(rampUpUsers).during(Duration(rampUpDuration, TimeUnit.SECONDS)),
      constantUsersPerSec(constantUsers).during(constantSeconds)
    )
  ).protocols(theHttpProtocolBuilder)
}
