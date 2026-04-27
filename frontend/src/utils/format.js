export const displayValue = (value) => (value === null || value === undefined || value === '' ? '-' : value);

export const formatDateTime = (value) => (value ? String(value).replace('T', ' ') : '-');

export const formatNumber = (value, digits = 2) => {
  if (value === null || value === undefined || value === '') return '-';
  const number = Number(value);
  if (Number.isNaN(number)) return value;
  return number.toLocaleString('zh-CN', {
    minimumFractionDigits: digits,
    maximumFractionDigits: digits,
  });
};

export const formatPercent = (value, digits = 2) => {
  if (value === null || value === undefined || value === '') return '-';
  const number = Number(value);
  if (Number.isNaN(number)) return value;
  return `${(number * 100).toFixed(digits)}%`;
};

export const profitColor = (value) => {
  const number = Number(value);
  if (Number.isNaN(number) || number === 0) return undefined;
  return number > 0 ? '#0f9f6e' : '#d4380d';
};

export const positionStatusMeta = (value) => {
  const map = {
    OPEN: { text: '持仓中', color: 'processing' },
    PARTIAL_CLOSED: { text: '部分平仓', color: 'warning' },
    CLOSED: { text: '已清仓', color: 'default' },
  };
  return map[value] || { text: displayValue(value), color: 'default' };
};
