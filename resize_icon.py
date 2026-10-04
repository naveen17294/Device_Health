from PIL import Image
import os

img_path = r'C:\Users\navee\.gemini\antigravity\brain\43a04224-abba-4ea6-9d09-81d5962cbe92\device_health_icon_1791036053359.jpg'
res_dir = r'C:\Users\navee\Documents\Projects\Device_Health\app\src\main\res'

sizes = {
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
    'xxxhdpi': 192
}

img = Image.open(img_path).convert('RGBA')

for dpi, size in sizes.items():
    resized = img.resize((size, size), Image.Resampling.LANCZOS)
    
    out_dir = os.path.join(res_dir, f'mipmap-{dpi}')
    os.makedirs(out_dir, exist_ok=True)
    resized.save(os.path.join(out_dir, 'ic_launcher.png'))
    
    mask = Image.new('L', (size, size), 0)
    from PIL import ImageDraw
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, size, size), fill=255)
    
    round_img = resized.copy()
    round_img.putalpha(mask)
    round_img.save(os.path.join(out_dir, 'ic_launcher_round.png'))

print('Icons generated!')
