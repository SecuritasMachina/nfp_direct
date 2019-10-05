import {Component, OnInit} from '@angular/core';
import {EditableTableModule} from 'angular-inline-editable-table';


@Component({
  selector: 'app-data-source-list',
  templateUrl: './data-source-list.component.html',
  styleUrls: ['./data-source-list.component.css']
})
export class DataSourceListComponent implements OnInit {

  constructor() {
  }

  private dataLoaded: boolean;
  private tableData: any;
  private yourMessage = [];



const CUSTOMER_DATA: tableData[] = [
  {id: 1, firstName: 'Ratnesh', updatedAt: new Date(), createdAt: new Date()},
  {id: 2, firstName: 'Zing', updatedAt: new Date(), createdAt: new Date()},
  {id: 3, firstName: 'Greg', updatedAt: new Date(), createdAt: new Date()},
  {id: 4, firstName: 'Sana', updatedAt: new Date(), createdAt: new Date()},
  {id: 5, firstName: 'Neha', updatedAt: new Date(), createdAt: new Date()},
  {id: 6, firstName: 'Kiran', updatedAt: new Date(), createdAt: new Date()},
  {id: 7, firstName: 'John', updatedAt: new Date(), createdAt: new Date()},
  {id: 8, firstName: 'Engliue', updatedAt: new Date(), createdAt: new Date()},
  {id: 9, firstName: 'Marina', updatedAt: new Date(), createdAt: new Date()},
  {id: 10, firstName: 'Vivek', updatedAt: new Date(), createdAt: new Date()},
];

ngOnInit();
{
  tableData = CUSTOMER_DATA;
  dataLoaded=true;
}

}
