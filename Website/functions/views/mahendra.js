var fs = require('fs');
console.log("sssssssss"+fs.readFileSync('input.txt','utf-8'));
fs.writeFileSync('write.txt',fs.readFileSync('readme.txt','utf-8'));
