import re

file_path = r'e:\Personal\PMS\SRS\14_Task_List.md'

with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

in_phase_3 = False
new_lines = []

for line in lines:
    if line.startswith('# PHASE 3'):
        in_phase_3 = True
    elif line.startswith('# PHASE 4'):
        in_phase_3 = False
    
    if in_phase_3 and line.strip().startswith('- [ ]'):
        line = line.replace('- [ ]', '- [x]', 1)
        
    new_lines.append(line)

with open(file_path, 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print("Phase 3 marked as complete!")
