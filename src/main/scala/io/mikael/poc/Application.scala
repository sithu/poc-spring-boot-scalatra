package io.mikael.poc

import java.sql.ResultSet
import javax.sql.DataSource

import org.json4s.{DefaultFormats, Formats}
import org.psnively.scala.jdbc.core.JdbcTemplate
import org.scalatra.ScalatraServlet
import org.scalatra.json.JacksonJsonSupport
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Repository

object Application {
  def main(args: Array[String]) {
    SpringApplication.run(classOf[Application], args:_*)
  }
}

@SpringBootApplication
class Application {

  @Bean
  def customServletRegistrationBean(customServlet : CustomServlet) = new ServletRegistrationBean(customServlet, "/*")

  @Bean
  def customServlet(dataSource : DataSource) = new CustomServlet(dataSource)

}

@Repository
class CustomServlet(dataSource : DataSource) extends ScalatraServlet with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  private val jdbcTemplate = new JdbcTemplate(dataSource)

  private val ALL_RESTAURANTS = "SELECT id, name FROM restaurants"

  private val MAP_RESTAURANT = (rs : ResultSet, i : Int) => Restaurant(rs.getInt(1), rs.getString(2))

  get("/") {
    "index page, SPA?"
  }

  get("/api/restaurants") {
    contentType = formats("json")
    jdbcTemplate.queryAndMap(ALL_RESTAURANTS)(MAP_RESTAURANT)
  }

}

case class Restaurant(id : Int, name : String)
