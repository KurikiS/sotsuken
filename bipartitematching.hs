{- bipartite matching problem -}

import Fregel
import Graphs

random dv [] = dv
random dv [x] = x
random dv (x:xs) = x

data SVal = SVal { match :: Int} deriving (Eq, Show)

bipartitematching g = 
  let init v = SVal(-1);
      ss0 v = SVal(if (vid v`mod`2 == 0 && val v.^match == -1)
                   then random (-1) [vid u|(e,u)<-is v,val u.^match == -1]
                   else val v.^match);
      ss1 v = SVal(if (vid v`mod`2 == 1 && val v.^match == -1) 
                   then random (-1) [vid u|(e,u)<-is v,val u.^match == vid v]
                   else val v.^match);
      ss2 v = SVal(if (vid v`mod`2 == 0 && val v.^match /= -1) 
                   then random (-1) [vid u|(e,u)<-is v,val u.^match == vid v] 
                   else val v.^match);   
      step g = let g1 = gmap ss0 g;
                   g2 = gmap ss1 g1;
                   g3 = gmap ss2 g2
               in g3
  in giter init step Fix g


graph :: Graph Int Int
graph = mG [(0,0),(1,0),(2,0),(3,0)]
           [(0,1,0),(1,2,0),(2,3,0)]
           True
