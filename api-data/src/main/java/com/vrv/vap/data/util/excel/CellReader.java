package com.vrv.vap.data.util.excel;

import com.vrv.vap.data.component.config.DictConfig;
import com.vrv.vap.data.model.SourceField;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

public class CellReader {


    private boolean isInteger = false;

    private boolean isDouble = false;

    private DictConfig dictConfig;

    private String dict = null;

    private SourceField field;

    public CellReader(SourceField field, DictConfig dictConfig) {
        this.field = field;
        if (StringUtils.isNotBlank(field.getDict())) {
            this.dictConfig = dictConfig;
            this.dict = field.getDict();
        }
        if ("long".equals(field.getType())) {
            this.isInteger = true;
        } else if ("double".equals(field.getType())) {
            this.isDouble = true;
        } else if ("tinyint".equals(field.getOrigin()) || "bit".equals(field.getOrigin())) {
            this.isInteger = true;
        }

    }

    public String getValue(Cell cell) {

        if (this.isInteger || this.isDouble) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    double value = cell.getNumericCellValue();
                    if (this.isInteger) {
                        return String.valueOf((long) value);
                    }
                    return String.valueOf(value);
                case STRING:
                    String str = this.dequote(cell.getStringCellValue());
                    if (str == null) {
                        return "null";
                    }
                    String val = "null";
                    if (dict != null) {
                        str = this.dictConfig.getCode(dict, str);
                    }
                    if (isInteger) {
                        try {
                            val = String.valueOf(Long.parseLong(str));
                        } catch (Exception e) {

                        }
                    }
                    if (isDouble) {
                        try {
                            val = String.valueOf(Double.parseDouble(str));
                        } catch (Exception e) {

                        }
                    }
                    return val;
            }

            return "null";
        }


        // 字段串形式
        switch (cell.getCellType()) {
            case NUMERIC:
                return "'" + (long)cell.getNumericCellValue() + "'";
            case STRING:
                String str = dequote(cell.getStringCellValue());
                if(str==null){
                    return "null";
                }
                return "'" + str + "'";
        }
        return "null";
    }


    private String dequote(String sD) {
        sD = sD.trim();
        while (sD.startsWith("\"")) sD = sD.substring(1);
        while (sD.endsWith("\"")) sD = sD.substring(0, sD.length() - 1);
        sD = sD.trim();
        if (sD.length() == 0) return null;
        return sD;
    }


}
