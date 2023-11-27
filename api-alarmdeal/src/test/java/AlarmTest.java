import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport.UpReportRegularService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.LogIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class AlarmTest {

    private  static Gson gson = new Gson();


    @Test
    public void testGroupIdByLogs() {
        UpReportRegularService service = new UpReportRegularService();
        List<String> logs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("guid", "guid" + i);
            map.put("test", "test" + i);
            logs.add(gson.toJson(map));
        }
        List<AlarmEventAttribute> docs = new ArrayList<>();
        AlarmEventAttribute doc1 = new AlarmEventAttribute();
        doc1.setEventId("1");
        List<LogIdVO> logIds1 = new ArrayList<>();
        LogIdVO logIdVO1 = new LogIdVO();
        logIdVO1.setLogGuids(Arrays.asList("guid1", "guid2"));
        logIds1.add(logIdVO1);
        doc1.setLogs(logIds1);
        docs.add(doc1);
        AlarmEventAttribute doc2 = new AlarmEventAttribute();
        doc2.setEventId("2");
        List<LogIdVO> logIds2 = new ArrayList<>();
        LogIdVO logIdVO2 = new LogIdVO();
        logIdVO2.setLogGuids(Arrays.asList("guid3", "guid4"));
        logIds2.add(logIdVO2);
        doc2.setLogs(logIds2);
        docs.add(doc2);
        Map<String, List<String>> result = service.groupIdByLogs(logs, docs);
        assertEquals(2, result.size());

    }



}
