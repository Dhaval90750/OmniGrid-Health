const fs = require('fs');
const path = require('path');

const srcDir = path.join(__dirname, 'src');

function findButtons(dir) {
    const files = fs.readdirSync(dir);
    
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            findButtons(fullPath);
        } else if (fullPath.endsWith('.tsx') || fullPath.endsWith('.jsx')) {
            const content = fs.readFileSync(fullPath, 'utf8');
            const lines = content.split('\n');
            let inButton = false;
            let buttonContent = '';
            let startLine = 0;
            
            for (let i = 0; i < lines.length; i++) {
                const line = lines[i];
                if (line.includes('<Button')) {
                    inButton = true;
                    buttonContent = line;
                    startLine = i + 1;
                } else if (inButton) {
                    buttonContent += ' ' + line;
                }
                
                if (inButton && (line.includes('</Button>') || line.includes('/>'))) {
                    inButton = false;
                    
                    // Check if the button has functionality
                    const isTypeSubmit = buttonContent.includes('type="submit"');
                    const hasOnClick = buttonContent.includes('onClick=');
                    const hasValidOnClick = buttonContent.match(/onClick=\{[^}]+}/) && !buttonContent.includes('onClick={() => {}}');
                    
                    // Allow buttons inside Link components? That's harder to check here, but let's just log those that look really dead.
                    if (!isTypeSubmit && (!hasOnClick || buttonContent.includes('onClick={() => {}}'))) {
                        console.log(`\nFile: ${fullPath.replace(__dirname, '')}:${startLine}`);
                        console.log(`Button: ${buttonContent.trim().replace(/\s+/g, ' ').substring(0, 150)}...`);
                    }
                }
            }
        }
    }
}

console.log("Scanning for dead buttons...\n");
findButtons(srcDir);
