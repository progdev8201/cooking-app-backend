# cooking-app-backend

Application created to manage cooking at home and doing grocery shopping

Backend Technologies:
  Spring boot,
  Spring security,
  Mongo db,
  Flapdoodle,
  aws ec2,
  docker,
  lombok
 
Frontend Technologies:
  Angular,
  Ng2-charts,
  Chart-Js,
  Angular Material,
  Bootstrap,
  oauth-jwt
  
  

Services:
  - article service:
    allow user to list articles,
    allow user to create articles,
    allow user to update articles,
    allow user to delete articles
    
  - routine service:
    allow user to create routine,
    allow user to add articles to routine,
    allow user to delete articles to routine,
    allow user to delete routine,
    allow user to take articles from routine then add to shopping
  
  - shopping service:
    allow user to have a list of articles to shop,
    allow user to shop article (virtual shop, will add a shopping transaction to the article for statistics purposes),
    allow user to remove article from shopping list
  
  - recipe service:
    allow user to list recipe,
    allow user to create recipe,
    allow user to edit recipe,
    allow user to delete recipe
    
  - fridge service:
    allow user to have a virtual fridge interface,
    allow user to see available articles at home,
    allow user to see cookable recipes based on available articles,
    allow user to send available article to missing article and view them
  
  - cooking service:
    allow user to chose a recipe and schedule a cooking date,
    allow user to see recipe basic info and cooking instructions,
    allow user to list recipes to cook
    
  - statistics service:
    allow user to see a dashboard with charts js and ng2-charts,
    allow user to see how much money he spent this year,
    allow user to know how many times he cooked this year,
    allow user to see the average time cook per month,
    allow user to see his money spent per month,
    allow user to see the amount of time a recipe is cooked
    
    
