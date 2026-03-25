// Функция считает разницу в днях между текущей датой и переданной. Принимает в себя дату в таком формате: '2026-02-02T12:18:11.936Z'
export const getDaysDiff = (dateStr: string): number => {
  const msInDay = 1000 * 60 * 60 * 24
  const diffInDays = Date.now() - new Date(dateStr).getTime()
  return Math.floor(diffInDays / msInDay)
}
