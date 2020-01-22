# LanDen Labs - UI Test  
Android UI Test  [Home website](http://landenlabs.com/android/index.html)  
20-Jan-2020
  
### Demonstrate Android Ui Features   
**All-uiTest** is an Android app which contains a set of demonstration pages. The pages implement  various containers such as **GridView**, **TableLayout**, **LinearLayout** and **GridLayout**.  
Each page either shows **Cell expansion transition animation** or other containers   features such as built-in **dividers**.   
  
The cell expansion grows the size of one or more cells without altering the parent container and its other children.   
  
There is also a demonstration of an overlay dialog which uses a **BitmapShader**,  **ColorMatrix**,  **ColorMatrixColorFilter**, and **Matrix**. A small  white image is translated using the matrix, is rendered on a larger   canvas using the BitmapShader **clamped** image to fill the canvas.   The **ColorMatrix** tints the white image. A clipping rectangle is   used to limit the rendering to leave a transparent band on the bottom.  [Click here to see example ](#page3_detail)
  
[![Build status](https://travis-ci.org/landenlabs/all-uitest.svg?branch=master)](https://travis-ci.org/landenlabs/all-uitest)
[![Known Vulnerabilities](https://snyk.io/test/github/landenlabs/all-uitest/badge.svg)](https://snyk.io/test/github/landenlabs/all-uitest)

API 28, pre Android X

## Key Android features demonstrated  
1. TransitionManager (animation)  
2. BitmapShader  
3. ColorMatrix, ColorMatrixColorFilter  
4. Matrix translation canvas draw  
5. Clipped canvas draw  
6. Dividers in LinearLayout and TableLayout  
7. 9 Patch (used for dividers)  
  
## Custom Views  
1. GridView - Added built-in dividers for both rows and columns.   
2. TextView - Shifts Tinted BitmapShader background and adds logo image  
3. GridLayout - Added built-in dividers, and hack to support **wrap_content** height  
   and **Lock** method to prevent children from moving when a cell is expanded.   
  
<a name="table"></a>  
## Table of Contents  
1. [GridView Layout - Expand cell transition animation](#page1)  
2. [Layout Dividers - TableLayout, LinearLayout and GridLayout with automatic dividers](#page2)  
3. [TableLayout and GridLayout - Expand cell transition animation](#page3)  
4. [TableLayout - Animate expanding a group of view cells](#page4)  
5. [TableLayout - Animate expanding an image of a group of view cells](#page5)  
6. [Shader - Demo using BitmapShader verse 9 Patch for background](#page6) 
7. [Border - Animate tracing container border](#page7) 
8. [Arc - Animated arc](#page8) 
***  
  
[Home webpage ](http://landenlabs.com/android/all-uitest/index.html) for detailed information.  

Also see [all-colormatrix](https://github.com/landenlabs/all-colormatrix) for demo on ColorMatrix
  
---  
<a name="page1"></a>  
### GridView Layout Cell background animation  
  
 Demonstrate attaching animated vector drawable to tapped cells. The animation shifts a gradient fill.
![picture](http://landenlabs.com/android/all-uitest/page1-tag.gif)  
  
[To Top](#table)  
  
  
#### GridView Layout Cell expand animation  
 Demonstrate expanding (**scaling** ) tapped cell. Expanded cell has its elevation increased so it floats over its peers and parent. 
![picture](http://landenlabs.com/android/all-uitest/page1-expand.gif)  
  
[To Top](#table)  
  
##### GridView Layout Cell detail floater  
 
 Demonstrate presentation of an overlay dialog which shifts a small background image to position an arrow over the center of the tapped cell, fill the dialog and include an icon image as if it spills off the bottom of the dialog. 
<img src="http://landenlabs.com/android/all-uitest/page1-details.gif">  
  
[To Top](#table)  
  
---  
<a name="page2"></a>  
### TableLayout and LinearLayout built-in dividers  
  
Both **TableLayout's** helper **TableRow** and **LinearLayout** support built-in cell dividers.  Using a **9 Patch** you can implement various divider looks.
  
<img src="http://landenlabs.com/android/all-uitest/page2-divider1.jpg" width="400">  
  
[To Top](#table)  
  
  #### Custom GridLayout built-in dividers  
  
<img src="http://landenlabs.com/android/all-uitest/page2-divider2.jpg" width="400">  
  
<img src="http://landenlabs.com/android/all-uitest/page2-divider3.jpg" width="400">  
  
[To Top](#table)  
  
  
---  
<a name="page3"></a>  
### TableLayout and GridLayout Cell background animation  
  
![picture](http://landenlabs.com/android/all-uitest/page3-tag.gif)  
  
[To Top](#table)  
  
<a name="page3_expand"></a>    
#### TableLayout and GridLayout  Cell expand animation  
  
![picture](http://landenlabs.com/android/all-uitest/page3-expand.gif)  
  
[To Top](#table)  
 <a name="page3_detail"></a>   
#### TableLayout and GridLayout Cell detail floater  
  
![picture](http://landenlabs.com/android/all-uitest/page3-details.gif)  
  
[To Top](#table)  
  
---  
<a name="page4"></a>  
### Group Grid selection Expansion   
Demonstrate group cell expansion by re-parenting the selected cells into a new container which is expanded. To prevent the original container from reflowing the reparented cells are replaced with Space views. The reverse is done when the expansion is cancelled. Example expanding a **column**. 

![picture](http://landenlabs.com/android/all-uitest/group-col.gif)  
  
[To Top](#table)  
  
Example expanding cells in a **"L"** shape. 

![picture](http://landenlabs.com/android/all-uitest/group-l.gif)  
  
[To Top](#table)  
  
Example expanding a **row**  
 
![picture](http://landenlabs.com/android/all-uitest/group-row.gif)  
  
[To Top](#table)  
  
---  
<a name="page5"></a>  
### Group Grid selection Expanded using image snapshot  
Demonstate animating the expansion of a group of cells using a snapshot image of them. 
This takes less code then previous approach and includes any dividers provided by parent container. 

![picture](http://landenlabs.com/android/all-uitest/page5-row.gif)  
  
[To Top](#table)  

![picture](http://landenlabs.com/android/all-uitest/page5-col.gif)  
  
[To Top](#table)  

![picture](http://landenlabs.com/android/all-uitest/page5-box.gif)  
  
[To Top](#table)  

---  
<a name="page6"></a>  
### Demonstrate using BitmapShader verses 9 Patch png for background  
Demonstate how a bitmap shader works better to fill a background when the background is shifted. 

![picture](http://landenlabs.com/android/all-uitest/page6-shader.jpg)  
  
[To Top](#table) 

---  
<a name="page7"></a>  
### Demonstrate animating highlight glow of layout border
Demonstate how to render an animated glow line around any layout.

![picture](http://landenlabs.com/android/all-uitest/page7-border.gif)  
  
[To Top](#table) 

---  
<a name="page8"></a>  
### Demonstrate animating an arc line
Demonstate how to render an animate arc guage. 

![picture](http://landenlabs.com/android/all-uitest/page8-arc.gif)  
  
[To Top](#table) 

# Web Page  
  
[Home web page](http://landenlabs.com/android/index.html) for more information.  
  
# License  
  
```  
Copyright 2019 Dennis Lang  
  
Licensed under the Apache License, Version 2.0 (the "License");  
you may not use this file except in compliance with the License.  
You may obtain a copy of the License at  
  
 http://www.apache.org/licenses/LICENSE-2.0  
Unless required by applicable law or agreed to in writing, software  
distributed under the License is distributed on an "AS IS" BASIS,  
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
See the License for the specific language governing permissions and  
limitations under the License.  
```   
  
[To Top](#table)  
<br>[Home website](http://landenlabs.com/android/index.html)