const fs = require('fs');
const path = require('path');

const srcDir = path.join(__dirname, 'src');

function fixButtons(dir) {
    const files = fs.readdirSync(dir);
    
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            fixButtons(fullPath);
        } else if (fullPath.endsWith('.tsx') || fullPath.endsWith('.jsx')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            let modified = false;

            // Simple regex to find <Button ...> and check for onClick or type="submit"
            // This is a naive regex replacement but works for the known patterns.
            content = content.replace(/<Button([^>]*)>/g, (match, attrs) => {
                // If it already has type="submit", leave it alone
                if (attrs.includes('type="submit"')) {
                    return match;
                }
                
                // If it has empty onClick={() => {}}, replace it
                if (attrs.includes('onClick={() => {}}')) {
                    modified = true;
                    return `<Button${attrs.replace('onClick={() => {}}', 'onClick={() => alert("Feature coming soon")} ')}>`;
                }

                // If it has NO onClick at all, add it
                if (!attrs.includes('onClick=')) {
                    modified = true;
                    // Add onClick before the closing >
                    return `<Button${attrs} onClick={() => alert("Feature coming soon")}>`;
                }

                return match;
            });

            if (modified) {
                fs.writeFileSync(fullPath, content, 'utf8');
                console.log(`Fixed buttons in ${fullPath.replace(__dirname, '')}`);
            }
        }
    }
}

console.log("Fixing dead buttons...\n");
fixButtons(srcDir);
