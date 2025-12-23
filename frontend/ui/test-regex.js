// 测试修复后的内存地址日志正则表达式
const memoryAddressPattern = /\[0x[0-9a-fA-F]+(\s+0x[0-9a-fA-F]+)+\]/;

// 测试用例
const testCases = [
  // 两个地址的日志（应该匹配）
  '[0xc007ba0440 0xc007ba0470]',
  '[0xc00477fb20 0xc00477fb50]',
  '[0xc0048e4020 0xc0048e4050]',
  
  // 三个地址的日志（应该匹配）
  '[0xc008aa5e70 0xc008aa5ea0 0xc008aa5ed0]',
  '[0xc0084e5540 0xc0084e5570 0xc0084e55a0]',
  '[0xc007b522d0 0xc007b52300 0xc007b52330]',
  
  // 四个地址的日志（应该匹配）
  '[0x1234 0x5678 0x9abc 0xdef0]',
  
  // 单个地址的日志（不应该匹配）
  '[0xc007ba0440]',
  
  // 非内存地址的日志（不应该匹配）
  '[some text]',
  '[1234 5678]',
  '[0x 0x]'
];

console.log('测试内存地址日志正则表达式:');
console.log('模式:', memoryAddressPattern);
console.log('\n测试结果:');
console.log('=' .repeat(60));

testCases.forEach((testCase, index) => {
  const isMatch = memoryAddressPattern.test(testCase);
  console.log(`测试 ${index + 1}: ${testCase}`);
  console.log(`匹配结果: ${isMatch ? '✅ 匹配' : '❌ 不匹配'}`);
  console.log('-' .repeat(60));
});

// 测试捕获组
console.log('\n捕获组测试:');
console.log('=' .repeat(60));

const sampleLogs = [
  '[0xc007ba0440 0xc007ba0470]',
  '[0xc007b522d0 0xc007b52300 0xc007b52330]'
];

sampleLogs.forEach((log, index) => {
  const match = log.match(memoryAddressPattern);
  console.log(`日志 ${index + 1}: ${log}`);
  console.log(`完整匹配: ${match[0]}`);
  console.log(`捕获组: ${match[1]}`);
  console.log('-' .repeat(60));
});
