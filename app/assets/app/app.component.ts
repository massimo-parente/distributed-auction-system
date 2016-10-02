import { Component } from '@angular/core';
@Component({
  selector: 'my-app',
  template: `
    <div class="container" role="main">
        <navbar></navbar>   
        <router-outlet></router-outlet>
    </div> 
  `
})
export class AppComponent { }