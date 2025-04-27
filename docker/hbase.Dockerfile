# Jobb alap HBase image: hivatalos HBase build alapján
FROM apache/hbase:2.4.17

COPY hbase-site.xml /opt/hbase/conf/hbase-site.xml