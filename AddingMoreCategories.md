# Introduction #

In this page we will quickly demonstrate how easy it is to include a new category, using a Bikeracks category as an example.

# Details #
  1. Easiest way: simply use FINsert at http://cubist.cs.washington.edu/projects/11wi/cse403/RecycleLocator/FINsert/category.php

> (Or you can)
> Create a new table named bikeracks in the database with the standard structure of FindItNow database tables.  That is, a query such as:
```
CREATE TABLE IF NOT EXISTS `bikeracks` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `rid` int(11) NOT NULL,
  `latitude` int(11) NOT NULL,
  `longitude` int(11) NOT NULL,
  `special_info` varchar(255) NOT NULL,
  `fid` int(11) NOT NULL,
  `not_found_count` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `rid` (`rid`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;
```


> 2. (OPTIONAL) Copy a category icon named bikeracks\_big.png and one named bikeracks.png to the /res/drawable-hdpi sub-directory of the project folder.  bikeracks\_big.png should be 80 pixels in height and no more than 80 in width, while bikeracks.png should be 60 pixels in height and no more than 60 in width

You're done!