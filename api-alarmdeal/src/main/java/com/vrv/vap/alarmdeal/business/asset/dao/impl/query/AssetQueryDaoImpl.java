package com.vrv.vap.alarmdeal.business.asset.dao.impl.query;

import com.vrv.vap.alarmdeal.business.asset.dao.query.AssetQueryDao;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetTotalStatisticsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetTypeTotalVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.SafeDeviceListVO;
import com.vrv.vap.jpa.web.NameValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class AssetQueryDaoImpl implements AssetQueryDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public AssetTotalStatisticsVO queryAssetTotalStatistics() {
        String sql = "select " +
                " (select count(guid)  from asset) as assetTotal," +
                " (select count(guid)  from asset where term_type='1') as termTypeCN," +
                " (select count(guid)  from asset where term_type='2') as termTypeEN ";
        AssetTotalStatisticsVO details = jdbcTemplate.queryForObject(sql, new AssetTotalStatisticsVOMapper());
        return details;
    }
    public class AssetTotalStatisticsVOMapper implements RowMapper<AssetTotalStatisticsVO> {
        @Override
        public AssetTotalStatisticsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetTotalStatisticsVO detail = new AssetTotalStatisticsVO();
            detail.setAssetTotal(rs.getString("assetTotal") == null?0:Integer.parseInt(rs.getString("assetTotal")));
            detail.setTermTypeCN(rs.getString("termTypeCN")==null?0:Integer.parseInt(rs.getString("termTypeCN")));
            detail.setTermTypeEN(rs.getString("termTypeEN")==null?0:Integer.parseInt(rs.getString("termTypeEN")));
            return detail;
        }
    }
    @Override
    public List<AssetStatisticsVO> queryAssetByAssetType() {
        String sql = "select tgroup.`Name` as typeName,count(*) as number " +
                " from asset as asset inner join " +
                " asset_type as type on type.Guid=asset.Type_Guid " +
                " inner join " +
                " asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
                " GROUP BY tgroup.TreeCode ";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }

    @Override
    public List<AssetStatisticsVO> queryAssetByDepartment() {
        String sql = "select org_name as typeName,count(*) as number from asset as asset  where org_name is not null and org_name != '' group by org_code ";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    @Override
    public List<AssetStatisticsVO> queryAssetByDepartmentType(String type) {
        String sql = "select org_name as typeName,count(*) as number from " +
                "asset as asset " +
                "INNER JOIN asset_type AS type ON type.Guid = asset.Type_Guid " +
                "INNER JOIN asset_type_group AS tgroup ON SUBSTRING_INDEX( type.TreeCode, '-', 2 ) = tgroup.TreeCode " +
                "where org_name is not null and org_name != '' and tgroup.TreeCode = '"+type +"' group by org_code ";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }

    @Override
    public List<AssetStatisticsVO> queryAssetNumByAssetType(String assetType) {
        String sql="select type.`Name` as typeName,count(*) as number from asset as asset inner join " +
                " asset_type as type on type.Guid=asset.Type_Guid WHERE TreeCode like '"+assetType+"%'  GROUP BY type.TreeCode ";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }

    @Override
    public List<AssetStatisticsVO> queryAssetByLevel(String assetParentType) {
        String sql="select dict.code_value as typeName, count(*) as number from  asset as asset inner join base_dict_all as dict on asset.equipment_intensive = dict.code" +
                " where  dict.parent_type='"+assetParentType+"' group by  dict.code_value";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    @Override
    public Integer countByWorth(Integer code) {
        String sql="select count(1) as count FROM asset WHERE asset.worth = '"+code+"'";
        return jdbcTemplate.queryForObject(sql,Integer.class);
    }



    @Override
    public List<AssetStatisticsVO> queryAssetTypeGroupByLevel(String type,String assetParentType) {
        String sql=" select dict.code_value as typeName, count(*) as number from  asset as asset inner join asset_type as type on type.Guid=asset.Type_Guid inner join " +
                " asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode inner join base_dict_all as dict on asset.equipment_intensive = dict.code" +
                " where  tgroup.TreeCode='" + type +"'" +
                " and dict.parent_type='" +assetParentType +"'" +
                " group by  dict.code_value";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    /**
     * 资产分类汇总统计：按资产类型统计 终端总数$，服务器总数$，网络设备总数$，安全设备总数$，其他设备数$。
     * @return
     */
    @Override
    public AssetTypeTotalVO queryAssetTypeTotal() {
        String sql = "select " +
                " (select count(*)  from asset as asset inner join asset_type as type on type.Guid=asset.Type_Guid " +
                " inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
                " where tgroup.TreeCode='asset-Host') as assetHost, " +
                " (select count(*)  from asset as asset inner join asset_type as type on type.Guid=asset.Type_Guid " +
                " inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
                " where tgroup.TreeCode='asset-service') as assetService, " +
                " (select count(*)  from asset as asset inner join asset_type as type on type.Guid=asset.Type_Guid " +
                " inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
                " where tgroup.TreeCode='asset-SafeDevice') as assetSafeDevice, " +
                " (select count(*)  from asset as asset inner join asset_type as type on type.Guid=asset.Type_Guid " +
                " inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
                " where tgroup.TreeCode='asset-NetworkDevice') as assetNetworkDevice, " +
                " (select count(*)  from asset as asset inner join asset_type as type on type.Guid=asset.Type_Guid " +
                " inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
                " where tgroup.TreeCode not in('asset-NetworkDevice','asset-Host','asset-service','asset-SafeDevice')) as otherAsset";
        AssetTypeTotalVO details = jdbcTemplate.queryForObject(sql, new AssetTypeTotalVOMapper());
        return details;
    }

    public class AssetTypeTotalVOMapper implements RowMapper<AssetTypeTotalVO> {
        @Override
        public AssetTypeTotalVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetTypeTotalVO detail = new AssetTypeTotalVO();
            detail.setAssetHost(rs.getString("assetHost") == null?0:Integer.parseInt(rs.getString("assetHost")));
            detail.setAssetNetworkDevice(rs.getString("assetNetworkDevice")==null?0:Integer.parseInt(rs.getString("assetNetworkDevice")));
            detail.setAssetSafeDevice(rs.getString("assetSafeDevice")==null?0:Integer.parseInt(rs.getString("assetSafeDevice")));
            detail.setAssetService(rs.getString("assetService")==null?0:Integer.parseInt(rs.getString("assetService")));
            detail.setOtherAsset(rs.getString("otherAsset")==null?0:Integer.parseInt(rs.getString("otherAsset")));
            return detail;
        }
    }
    /**
     * 一级资产类型下 ，按照二级资产类型、国产非国产进行分类统计
     * @param assetTypeGroupTreeCode 一级资产类型treeCode
     * @return Result
     */
    @Override
    public List<AssetStatisticsVO> queryAssetTypeTotalByTermType(String assetTypeGroupTreeCode) {
        String sql = "select concat(termType,typeName) as typeName,number from( " +
                " select  " +
                "  CASE asset.term_type   " +
                "        WHEN '1' THEN '国产'  " +
                "        WHEN '2' THEN '非国产' " +
                " end  termType," +
                " type.name as typeName," +
                " count(asset.guid) as number" +
                " from  asset as asset inner join " +
                " asset_type as type on type.Guid=asset.Type_Guid " +
                " inner join" +
                " asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
                " where  asset.term_type is not null and asset.term_type!='' and tgroup.TreeCode='" +
                assetTypeGroupTreeCode+
                "' group BY" +
                " CASE asset.term_type  " +
                "        WHEN '1' THEN '国产 '  " +
                "        WHEN '2' THEN '非国产'  " +
                " END  " +
                " ,type.TreeCode )as datas ";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    /**
     * 其他设备数量按类型统计
     * 其他设备  ：刻录机、打印机、涉密专用介质
     * @return
     */
    @Override
    public List<AssetStatisticsVO> queryOtherAssetNumber() {
        String sql = "select  " +
                "CASE type.TreeCode  " +
                "        WHEN 'asset-OfficeDevice-WriterMachine' THEN '刻录机' " +
                "        WHEN 'asset-OfficeDevice-printer' THEN '打印机' " +
                "        WHEN 'asset-USBMemory-classified' THEN '涉密专用介质' " +
                "end as typeName," +
                "count(asset.guid) as number " +
                "from asset as asset inner join " +
                "asset_type as type on type.Guid=asset.Type_Guid " +
                "where type.TreeCode in('asset-OfficeDevice-WriterMachine','asset-OfficeDevice-printer','asset-USBMemory-classified') " +
                "GROUP BY " +
                "CASE type.TreeCode  " +
                "        WHEN 'asset-OfficeDevice-WriterMachine' THEN '刻录机' " +
                "        WHEN 'asset-OfficeDevice-printer' THEN '打印机' " +
                "        WHEN 'asset-USBMemory-classified' THEN '涉密专用介质' " +
                "end";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    /**
     * 安全设备信息列表
     *
     * @return
     */
    @Override
    public List<SafeDeviceListVO> querySafeDeviceAssetList() {
        String sql = "select  " +
                "asset.name as assetName,type.name as assetType, " +
                "JSON_UNQUOTE(JSON_EXTRACT(extend.extendInfos,'$.extendVersionInfo')) AS assetVersion, " +
                "JSON_UNQUOTE(JSON_EXTRACT(extend.extendInfos,'$.extendTypeSno')) AS firmName " +
                "from  asset as asset inner join  " +
                "asset_type as type on type.Guid=asset.Type_Guid " +
                "inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
                "left join  asset_extend extend on extend.assetGuid=asset.guid " +
                "where  tgroup.TreeCode='asset-SafeDevice' ";
        List<SafeDeviceListVO> details = jdbcTemplate.query(sql, new SafeDeviceListVOMapper());
        return details;
    }


    public class AssetStatisticsVOMapper implements RowMapper<AssetStatisticsVO> {
        @Override
        public AssetStatisticsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetStatisticsVO detail = new AssetStatisticsVO();
            detail.setName(rs.getString("typeName") );
            detail.setCount(rs.getString("number")==null?0:Integer.parseInt(rs.getString("number")));
            return detail;
        }
    }

    public class SafeDeviceListVOMapper implements RowMapper<SafeDeviceListVO> {
        @Override
        public SafeDeviceListVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            SafeDeviceListVO detail = new SafeDeviceListVO();
            detail.setName(rs.getString("assetName") );
            detail.setType(rs.getString("assetType") );
            detail.setVersion(rs.getString("assetVersion") );
            detail.setCompanyName(rs.getString("firmName") );
            return detail;
        }
    }
    /**
     * 资产数量按安全域统计
     * 2023-07-04
     * @return
     */
    @Override
    public List<AssetStatisticsVO> queryAssetByDomain() {
        String sql = "select domain.domain_name as typeName ,count(*) as number from asset inner join base_security_domain as domain " +
                "on asset.securityGuid=domain.code group by  domain.domain_name";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    /**
     * 其他设备类型统计（除终端、服务器、网络设备、安全设备一级资产类型外的设备）
     *  2023-07-04
     * @return
     */
    @Override
    public List<AssetStatisticsVO> queryAssetByOther() {
        String sql = "select  type.Name as typeName,count(asset.guid) as number from asset as asset inner join  asset_type as type on type.Guid=asset.Type_Guid " +
                "inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
                "where tgroup.TreeCode not in('asset-Host','asset-service','asset-NetworkDevice','asset-SafeDevice') GROUP BY type.Name" ;
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    @Override
    public List<NameValue> queryAssetByArea() {
        String sql="SELECT base_security_domain.domain_name as typeName,count(asset.ip) as number FROM asset  INNER  JOIN base_security_domain on  base_security_domain.code=asset.securityGuid GROUP BY asset.securityGuid";
        List<NameValue> details = jdbcTemplate.query(sql, new NameValueMapper());
        return details;
    }



    public class NameValueMapper implements RowMapper<NameValue> {
        @Override
        public NameValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            NameValue detail = new NameValue();
            detail.setName(rs.getString("typeName") );
            detail.setValue(rs.getString("number")==null?"0":rs.getString("number"));
            return detail;
        }
    }
}
