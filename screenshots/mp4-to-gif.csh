#!/bin/csh -f

set inMP4=$1
set outGIF=$2

ffmpeg -i $inMP4 -r 1 -vf scale=540:960 foo.gif -hide_banner
convert -delay 50x100 foo.gif $outGIF
rm foo.gif
    
    