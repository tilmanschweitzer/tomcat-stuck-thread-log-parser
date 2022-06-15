load test_helper.bash

# ===== fgrep as reference test =====
@test "Command 'fgrep -c notifyStuckThreadDetected' parses example files as expected" {
  run grep -c notifyStuckThreadDetected tomcat-log-examples/*
  [ "$status" -eq 0 ]
  [ "${lines[0]}" = "$output_line_1" ]
  [ "${lines[1]}" = "$output_line_2" ]
  [ "${lines[2]}" = "$output_line_3" ]
  [ "${lines[3]}" = "$output_line_4" ]
  [ "${lines[4]}" = "$output_line_5" ]
  [ "${lines[5]}" = "$output_line_6" ]
  [ "${lines[6]}" = "$output_line_7" ]
  [ "${lines[7]}" = "$output_line_8" ]
  [ "${lines[8]}" = "$output_line_9" ]
  [ "${lines[9]}" = "$output_line_10" ]
  [ "${lines[10]}" = "$output_line_11" ]
  [ "${lines[11]}" = "$output_line_12" ]
  [ "${lines[12]}" = "$output_line_13" ]
  [ "${lines[13]}" = "$output_line_14" ]
  [ "${lines[14]}" = "$output_line_15" ]
  [ "${lines[15]}" = "$output_line_16" ]
  [ "${lines[16]}" = "$output_line_17" ]
}

# ===== Go tests =====

@test "Command 'go run go/tstlp-sync.go' parses example files as expected" {
  run go run go/tstlp-sync.go tomcat-log-examples
  [ "$status" -eq 0 ]
  [ "${lines[0]}" = "$output_line_1" ]
  [ "${lines[1]}" = "$output_line_2" ]
  [ "${lines[2]}" = "$output_line_3" ]
  [ "${lines[3]}" = "$output_line_4" ]
  [ "${lines[4]}" = "$output_line_5" ]
  [ "${lines[5]}" = "$output_line_6" ]
  [ "${lines[6]}" = "$output_line_7" ]
  [ "${lines[7]}" = "$output_line_8" ]
  [ "${lines[8]}" = "$output_line_9" ]
  [ "${lines[9]}" = "$output_line_10" ]
  [ "${lines[10]}" = "$output_line_11" ]
  [ "${lines[11]}" = "$output_line_12" ]
  [ "${lines[12]}" = "$output_line_13" ]
  [ "${lines[13]}" = "$output_line_14" ]
  [ "${lines[14]}" = "$output_line_15" ]
  [ "${lines[15]}" = "$output_line_16" ]
  [ "${lines[16]}" = "$output_line_17" ]
}

@test "Command 'go run go/tstlp-parallel.go' parses example files as expected" {
  run go run go/tstlp-parallel.go tomcat-log-examples
  [ "$status" -eq 0 ]
  [ "${lines[0]}" = "$output_line_1" ]
  [ "${lines[1]}" = "$output_line_2" ]
  [ "${lines[2]}" = "$output_line_3" ]
  [ "${lines[3]}" = "$output_line_4" ]
  [ "${lines[4]}" = "$output_line_5" ]
  [ "${lines[5]}" = "$output_line_6" ]
  [ "${lines[6]}" = "$output_line_7" ]
  [ "${lines[7]}" = "$output_line_8" ]
  [ "${lines[8]}" = "$output_line_9" ]
  [ "${lines[9]}" = "$output_line_10" ]
  [ "${lines[10]}" = "$output_line_11" ]
  [ "${lines[11]}" = "$output_line_12" ]
  [ "${lines[12]}" = "$output_line_13" ]
  [ "${lines[13]}" = "$output_line_14" ]
  [ "${lines[14]}" = "$output_line_15" ]
  [ "${lines[15]}" = "$output_line_16" ]
  [ "${lines[16]}" = "$output_line_17" ]
}

# ===== Java tests =====

@test "Command 'java -jar java/target/tomcat-stuck-thread-log-parser-*-SNAPSHOT-jar-with-dependencies.jar' parses example files as expected" {
  run java -jar java/target/tomcat-stuck-thread-log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar tomcat-log-examples/
  [ "$status" -eq 0 ]
  [ "${lines[0]}" = "$output_line_1" ]
  [ "${lines[1]}" = "$output_line_2" ]
  [ "${lines[2]}" = "$output_line_3" ]
  [ "${lines[3]}" = "$output_line_4" ]
  [ "${lines[4]}" = "$output_line_5" ]
  [ "${lines[5]}" = "$output_line_6" ]
  [ "${lines[6]}" = "$output_line_7" ]
  [ "${lines[7]}" = "$output_line_8" ]
  [ "${lines[8]}" = "$output_line_9" ]
  [ "${lines[9]}" = "$output_line_10" ]
  [ "${lines[10]}" = "$output_line_11" ]
  [ "${lines[11]}" = "$output_line_12" ]
  [ "${lines[12]}" = "$output_line_13" ]
  [ "${lines[13]}" = "$output_line_14" ]
  [ "${lines[14]}" = "$output_line_15" ]
  [ "${lines[15]}" = "$output_line_16" ]
  [ "${lines[16]}" = "$output_line_17" ]
}

# ===== Node.js tests =====

@test "Command 'node/tstlp-worker-threads.mjs' parses example files as expected" {
  run node node/tstlp-worker-threads.mjs tomcat-log-examples/
  [ "$status" -eq 0 ]
  [ "${lines[0]}" = "$output_line_1" ]
  [ "${lines[1]}" = "$output_line_2" ]
  [ "${lines[2]}" = "$output_line_3" ]
  [ "${lines[3]}" = "$output_line_4" ]
  [ "${lines[4]}" = "$output_line_5" ]
  [ "${lines[5]}" = "$output_line_6" ]
  [ "${lines[6]}" = "$output_line_7" ]
  [ "${lines[7]}" = "$output_line_8" ]
  [ "${lines[8]}" = "$output_line_9" ]
  [ "${lines[9]}" = "$output_line_10" ]
  [ "${lines[10]}" = "$output_line_11" ]
  [ "${lines[11]}" = "$output_line_12" ]
  [ "${lines[12]}" = "$output_line_13" ]
  [ "${lines[13]}" = "$output_line_14" ]
  [ "${lines[14]}" = "$output_line_15" ]
  [ "${lines[15]}" = "$output_line_16" ]
  [ "${lines[16]}" = "$output_line_17" ]
}
