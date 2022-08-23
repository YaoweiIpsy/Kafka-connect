package com.bfa.kafka.connector.file

import spock.lang.Specification

class FileStreamSourceConnectorSpec extends Specification {
  def "check Version"() {
    when:
    def connector = new FileStreamSourceConnector()
    then:
    assert connector.version() != null
  }
}
