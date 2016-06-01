package my_vaadin.app;

import java.util.List;

import javax.servlet.annotation.WebServlet;


import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;



/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset("my_vaadin.app.MyAppWidgetset")
public class MyUI extends UI {

	private CustomerService service = CustomerService.getInstance();
	private Grid grid = new Grid();
	
	
	private TextField filterText = new TextField();
	CustmerForm form = new CustmerForm(this);
	
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();
		final CssLayout filtering = new CssLayout();

		grid.setColumns("firstName", "lastName", "email");
		
		
		filterText.setInputPrompt("filter by name...");
		filterText.addTextChangeListener(e -> {
			grid.setContainerDataSource(new BeanItemContainer<>(Customer.class,
					service.findAll(e.getText())));
		});
		
		Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
		clearFilterTextBtn.setDescription("clear the current filter...");
		clearFilterTextBtn.addClickListener(e -> {
			  filterText.clear();
			  updateList();
			});
		
		// add a selection listener to the grid for editing the existing one
		grid.addSelectionListener(event -> {
		    if (event.getSelected().isEmpty()) {
		        form.setVisible(false);
		    } else {
		        Customer customer = (Customer) event.getSelected().iterator().next();
		        form.setCustomer(customer);
		    }
		});
		
		// In order to add new customer, we add a button
		Button addCustomerBtn = new Button("Add new customer");
		addCustomerBtn.setDescription("Add new customer");
		addCustomerBtn.addClickListener(e -> {
		    grid.select(null);
		    form.setCustomer(new Customer());
		});
		// formating the buttons
		HorizontalLayout toolbar = new HorizontalLayout(filtering, addCustomerBtn);
		toolbar.setSpacing(true);
		
		filtering.addComponents(filterText, clearFilterTextBtn);
		filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		
		
		// add Grid and filtering to the layout
		//layout.addComponents(filtering, grid);
		HorizontalLayout main = new HorizontalLayout(grid, form);
		main.setSpacing(true);
		main.setSizeFull();
		grid.setSizeFull();
		main.setExpandRatio(grid, 1);
		// set form be invisible
		form.setVisible(false);
		
		layout.addComponents(toolbar, main);
		layout.setMargin(true);
		setContent(layout);
	}

	public void updateList() {
		// fetch list of Customers from service and assign it to Grid
		List<Customer> customers = service.findAll(filterText.getValue());
		grid.setContainerDataSource(new BeanItemContainer<>(Customer.class, customers));
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
	
	
}
