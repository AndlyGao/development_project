const zb = [
  {value: '2111', label: '表 - 封面', rank: '1'},
  {value: '2112', label: '表 - 扉页', rank: '1'},
  {value: '2113', label: '表 - 总说明', rank: '1'},
  {value: '', label: '',  rank: '2'},
  {value: '2121', label: '表 - 分部分项工程和单价措施项目清单计价表',  rank: '3'},
  {value: '2122', label: '表 - 总价措施项目清单与计价表',  rank: '3'},
  {value: '2123', label: '表 - 其他项目清单与计价汇总表',  rank: '3'},
  {value: '2124', label: '表 - 暂列金额明细表',  rank: '3'},
  {value: '2125', label: '表 - 材料（工程设备）暂估单价及调整表',  rank: '3'},
  {value: '2126', label: '表 - 专业工程工程暂估价明细表',  rank: '3'},
  {value: '2127', label: '表 - 计日工表',  rank: '3'},
  {value: '2128', label: '表 - 总承包服务费计价表',  rank: '3'},
  {value: '2129', label: '表 - 税金项目清单与计价表',  rank: '3'},
  {value: '21210', label: '表 - 发包人提供材料和工程设备一览表',  rank: '3'},
  {value: '21211', label: '表 - 承包人提供主要材料和工程设备一览表（适用于造价信息差额调整法）',  rank: '3'},
  {value: '21212', label: '表 - 承包人提供主要材料和工程设备一览表（适用于价格指数差额调整法）',  rank: '3'}
]
const tb = [
  {value: '2211', label: '表 - 封面', rank: '1'},
  {value: '2212', label: '表 - 扉页', rank: '1'},
  {value: '2213', label: '表 - 总说明', rank: '1'},
  {value: '2214', label: '表 - 建设项目投标报价汇总表', rank: '1'},
  {value: '2215', label: '表 - 工程项目分部分项、措施项目投标报价汇总表', rank: '1'},
  {value: '2216', label: '表 - 投标报价分项汇总表', rank: '1'},
  {value: '2221', label: '表 - 单项工程投标报价汇总表', rank: '2'},
  {value: '2231', label: '表 - 单位工程投标报价汇总表',  rank: '3'},
  {value: '2232', label: '表 - 分部分项工程和单价措施项目清单与计价表',  rank: '3'},
  {value: '2233', label: '表 - 综合单价分析表',  rank: '3'},
  {value: '2234', label: '表 - 总价措施项目清单与计价表',  rank: '3'},
  {value: '2235', label: '表 - 其他项目清单与计价汇总表',  rank: '3'},
  {value: '2236', label: '表 - 暂列金额明细表',  rank: '3'},
  {value: '2237', label: '表 - 材料（工程设备）暂估单价及调整表',  rank: '3'},
  {value: '2238', label: '表 - 专业工程工程暂估价明细表',  rank: '3'},
  {value: '2239', label: '表 - 计日工表',  rank: '3'},
  {value: '22310', label: '表 - 总承包服务费计价表',  rank: '3'},
  {value: '22311', label: '表 - 税金项目清单与计价表',  rank: '3'},
  {value: '22312', label: '表 - 发包人提供材料和工程设备一览表',  rank: '3'},
  {value: '22313', label: '表 - 承包人提供主要材料和工程设备一览表（适用于造价信息差额调整法）',  rank: '3'},
  {value: '22314', label: '表 - 承包人提供主要材料和工程设备一览表（适用于价格指数差额调整法）',  rank: '3'},
]
const xj = [
  {value: '2311', label: '表 - 封面', rank: '1'},
  {value: '2312', label: '表 - 扉页', rank: '1'},
  {value: '2313', label: '表 - 总说明', rank: '1'},
  {value: '2314', label: '表 - 建设项目招标控制价汇总表', rank: '1'},
  {value: '2315', label: '表 - 工程项目分部分项、措施项目招标控制价汇总表', rank: '1'},
  {value: '2316', label: '表 - 招标控制价分项汇总表', rank: '1'},
  {value: '2321', label: '表 - 单项工程招标控制价汇总表', rank: '2'},
  {value: '2331', label: '表 - 单位工程招标控制价汇总表',  rank: '3'},
  {value: '2332', label: '表 - 分部分项工程和单价措施项目清单与计价表',  rank: '3'},
  {value: '2333', label: '表 - 综合单价分析表',  rank: '3'},
  {value: '2334', label: '表 - 总价措施项目清单与计价表',  rank: '3'},
  {value: '2335', label: '表 - 其他项目清单与计价汇总表',  rank: '3'},
  {value: '2336', label: '表 - 暂列金额明细表',  rank: '3'},
  {value: '2337', label: '表 - 材料（工程设备）暂估单价及调整表',  rank: '3'},
  {value: '2338', label: '表 - 专业工程工程暂估价明细表',  rank: '3'},
  {value: '2339', label: '表 - 计日工表',  rank: '3'},
  {value: '23310', label: '表 - 总承包服务费计价表',  rank: '3'},
  {value: '23311', label: '表 - 税金项目清单与计价表',  rank: '3'},
  {value: '23312', label: '表 - 发包人提供材料和工程设备一览表',  rank: '3'},
  {value: '23313', label: '表 - 承包人提供主要材料和工程设备一览表（适用于造价信息差额调整法）',  rank: '3'},
  {value: '23314', label: '表 - 承包人提供主要材料和工程设备一览表（适用于价格指数差额调整法）',  rank: '3'},
]
export {
  zb, tb, xj
}
