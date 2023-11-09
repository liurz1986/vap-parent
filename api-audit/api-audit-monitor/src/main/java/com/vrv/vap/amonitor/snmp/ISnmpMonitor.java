package com.vrv.vap.amonitor.snmp;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface ISnmpMonitor {

    default VariableBinding[] getSnmpInfo(String... oids) {
        PDU pdu = getSnmpVersion() < 3 ? new PDUv1() : new ScopedPDU();
        pdu.setType(PDU.GET);
        for (int i = 0; i < oids.length; i++) {
            pdu.addOID(new VariableBinding(new OID(oids[i].trim())));
        }

        ResponseEvent responseEvent = null;
        try {
            responseEvent = getSnmp().get(pdu, getTarget());
        } catch (IOException e) {
            return null;
        }

        if (responseEvent.getResponse() == null) {
            return null;
        }
        VariableBinding[] variableBindings = responseEvent.getResponse().toArray();
        return variableBindings;
    }

    default VariableBinding[] getSnmpInfoNext(String... oids) {
        PDU pdu = getSnmpVersion() < 3 ? new PDUv1() : new ScopedPDU();
        pdu.setType(PDU.GET);
        for (int i = 0; i < oids.length; i++) {
            pdu.addOID(new VariableBinding(new OID(oids[i].trim())));
        }

        ResponseEvent responseEvent = null;
        try {
            responseEvent = getSnmp().getNext(pdu, getTarget());
        } catch (IOException e) {
            return null;
        }

        if (responseEvent.getResponse() == null) {
            return null;
        }
        VariableBinding[] variableBindings = responseEvent.getResponse().toArray();
        return variableBindings;
    }

    default List<VariableBinding[]> tableSnmpInfo(String... oids) {
        TableUtils tutils = new TableUtils(getSnmp(), new DefaultPDUFactory(getSnmpVersion() < 2 ? PDU.GET : PDU.GETBULK));
        OID[] columns = new OID[oids.length];
        for (int i = 0; i < oids.length; i++) {
            columns[i] = new VariableBinding(new OID(oids[i].trim())).getOid();
        }

        List<TableEvent> list = tutils.getTable(getTarget(), columns, null, null);
        List<VariableBinding[]> res = new ArrayList<>(list.size());
        for (TableEvent e : list) {
            VariableBinding[] vb = e.getColumns();
            if (vb != null) {
                res.add(vb);
            }
        }
        return res;
    }

    /**
     * 根据数学表达式计算结果
     *
     * @param ex
     * @param params
     * @return
     */
    default Object evaluate(Expression ex, Object... params) {
        JexlContext jc = new MapContext();
        for (int i = 0; i < params.length; i++) {
            jc.set("v" + (i + 1), params[i]);
        }
        return ex.evaluate(jc);
    }

    void monitorAll();

    int getSnmpVersion();

    Snmp getSnmp();

    Target getTarget();

    String uptime();

    boolean testSnmp();

    Object testSnmp(String... oids);

}
