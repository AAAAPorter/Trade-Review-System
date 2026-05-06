// 页面统一用 '-' 表示空值，避免表格里出现 null/undefined。
export const displayValue = (value) => (value === null || value === undefined || value === '' ? '-' : value);

// 后端 LocalDateTime 会序列化成 2026-01-01T09:30:00，这里转换成更适合展示的空格格式。
export const formatDateTime = (value) => (value ? String(value).replace('T', ' ') : '-');

// 数字展示统一走中文区域格式，便于千分位和小数位保持一致。
export const formatNumber = (value, digits = 2) => {
  if (value === null || value === undefined || value === '') return '-';
  const number = Number(value);
  if (Number.isNaN(number)) return value;
  return number.toLocaleString('zh-CN', {
    minimumFractionDigits: digits,
    maximumFractionDigits: digits,
  });
};

// 后端收益率以小数保存，例如 0.1234 表示 12.34%。
export const formatPercent = (value, digits = 2) => {
  if (value === null || value === undefined || value === '') return '-';
  const number = Number(value);
  if (Number.isNaN(number)) return value;
  return `${(number * 100).toFixed(digits)}%`;
};

// 盈亏颜色规则：正数绿、负数红、零或非数字不强调。
export const profitColor = (value) => {
  const number = Number(value);
  if (Number.isNaN(number) || number === 0) return undefined;
  return number > 0 ? '#0f9f6e' : '#d4380d';
};

// 后端保存的是状态码，前端在这里映射成 Ant Design Tag 需要的文案和颜色。
export const positionStatusMeta = (value) => {
  const map = {
    OPEN: { text: '持仓中', color: 'processing' },
    PARTIAL_CLOSED: { text: '部分平仓', color: 'warning' },
    CLOSED: { text: '已清仓', color: 'default' },
  };
  return map[value] || { text: displayValue(value), color: 'default' };
};
